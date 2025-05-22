package com.example.trace.gpt.service;

import com.example.trace.global.errorcode.GptErrorCode;
import com.example.trace.global.errorcode.PostErrorCode;
import com.example.trace.global.exception.GptException;
import com.example.trace.global.exception.PostException;
import com.example.trace.gpt.domain.Verification;
import com.example.trace.gpt.dto.VerificationDto;
import com.example.trace.post.dto.post.PostCreateDto;
import com.example.trace.user.User;
import com.example.trace.auth.repository.UserRepository;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Base64;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostVerificationServiceImpl implements PostVerificationService {

    private final OpenAiService openAiService;
    private final UserRepository userRepository;
    private static final String MODEL = "gpt-4o";
    
    @Value("${openai.api.key}")
    private String openaiApiKey;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public VerificationDto verifyPost(PostCreateDto postCreateDto,String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new PostException(PostErrorCode.USER_NOT_FOUND));

        if (postCreateDto.getContent() == null || postCreateDto.getContent().isEmpty()) {
            throw new PostException(PostErrorCode.CONTENT_EMPTY);
        }
        if (postCreateDto.getTitle() == null || postCreateDto.getTitle().isEmpty()) {
            throw new PostException(PostErrorCode.TITLE_EMPTY);
        }

        String content = postCreateDto.getContent();
        List<MultipartFile> images = postCreateDto.getImageFiles();

        if (images == null || images.isEmpty()) {
            // Only text verification is needed
            VerificationDto result = verifyTextOnly(content);
            if(!result.isTextResult()){
                String failureReason = result.getFailureReason();
                throw new GptException(GptErrorCode.WRONG_CONTENT,failureReason);
            }
            return result;
        } else {
            // Both text and image verification is needed
            VerificationDto result = verifyTextAndImages(content, images);
            if (!result.isTextResult() || !result.isImageResult()) {
                String failureReason = result.getFailureReason();
                throw new GptException(GptErrorCode.WRONG_CONTENT,failureReason);
            }
            return result;
        }
    }

    public Verification makeVerification(VerificationDto verificationDto){
        if(verificationDto.isTextResult() && verificationDto.isImageResult()){
            Verification verification = Verification.builder()
                    .isTextVerified(true)
                    .isImageVerified(true)
                    .failureReason(verificationDto.getFailureReason())
                    .successReason(verificationDto.getSuccessReason())
                    .build();
            return verification;
        }
        else if (verificationDto.isTextResult() && !verificationDto.isImageResult()) {
            Verification verification = Verification.builder()
                    .isTextVerified(true)
                    .isImageVerified(false)
                    .failureReason(verificationDto.getFailureReason())
                    .successReason(verificationDto.getSuccessReason())
                    .build();
            return verification;
        }
        else if(!verificationDto.isTextResult() && !verificationDto.isImageResult()){
            Verification verification = Verification.builder()
                    .isTextVerified(false)
                    .isImageVerified(false)
                    .failureReason(verificationDto.getFailureReason())
                    .successReason(verificationDto.getSuccessReason())
                    .build();
            return verification;
        }
        else {
            throw new GptException(GptErrorCode.GPT_LOGIC_ERROR,null);
        }
    }

    
    private VerificationDto verifyTextOnly(String content) {
        List<ChatMessage> messages = new ArrayList<>();
        
        String systemPrompt = "You are an AI assistant tasked with verifying if the given text describes an act of kindness. " +
                "Respond in the following format exactly:\n" +
                "text_result: true/false\n" +
                "success_reason: [reason for success, only if text_result is true]\n" +
                "failure_reason: [reason for failure, only if text_result is false]";

        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt));

        String userPrompt = "Please verify if the following text describes an act of kindness:\n\n" + content;
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), userPrompt));

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(MODEL)
                .messages(messages)
                .build();
        
        try {
            String response = openAiService.createChatCompletion(request).getChoices().get(0).getMessage().getContent();
            return parseVerificationResponse(response);
        } catch (Exception e) {
            log.error("Error verifying text with OpenAI", e);
            return VerificationDto.textOnlyFailure("Failed to verify text: " + e.getMessage());
        }
    }
    
    private VerificationDto verifyTextAndImages(String content, List<MultipartFile> images) {
        try {
            List<ChatMessage> messages = new ArrayList<>();
            
            String systemPrompt = "You are an AI assistant tasked with verifying if the given text describes an act of kindness " +
                    "and if the provided images properly describe the text and relate to acts of kindness. " +
                    "Respond in the following format exactly:\n" +
                    "text_result: true/false\n" +
                    "image_result: true/false\n" +
                    "success_reason: [reason for success, only if any result is true]\n" +
                    "failure_reason: [reason for failure, only if any result is false]";

            messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt));

            // User message with text content
            String userPrompt = "Please verify the following:\n\n" +
                    "1. Does the text describe an act of kindness?\n" +
                    "2. Do the images properly describe the text and relate to acts of kindness?\n\n" +
                    "Text: " + content;
            
            ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), userPrompt);
            messages.add(userMessage);

            // We'll need to build a multimodal request
            // Since older versions of the client don't support multimodal content directly,
            // we'll need to use a direct REST call for the images part
            
            if (images != null && !images.isEmpty()) {
                // For multimodal requests with images, we need to use direct REST API call
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Bearer " + openaiApiKey);
                
                // API request body
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", MODEL);
                requestBody.put("max_tokens", 500);
                
                // Messages array
                List<Map<String, Object>> apiMessages = new ArrayList<>();
                
                // System message
                Map<String, Object> systemMessage = new HashMap<>();
                systemMessage.put("role", "system");
                systemMessage.put("content", systemPrompt);
                apiMessages.add(systemMessage);
                
                // User message with text and images
                Map<String, Object> apiUserMessage = new HashMap<>();
                apiUserMessage.put("role", "user");
                
                List<Map<String, Object>> contentItems = new ArrayList<>();
                
                // Text content
                Map<String, Object> textContent = new HashMap<>();
                textContent.put("type", "text");
                textContent.put("text", userPrompt);
                contentItems.add(textContent);
                
                // Image content
                for (MultipartFile image : images) {
                    try {
                        String base64Image = encodeMultipartFileToBase64(image);
                        if (base64Image != null) {
                            Map<String, Object> imageContent = new HashMap<>();
                            imageContent.put("type", "image_url");
                            
                            Map<String, String> imageUrl = new HashMap<>();
                            imageUrl.put("url", "data:image/jpeg;base64," + base64Image);
                            log.info("Image base64 length: {}", base64Image.length());
                            
                            imageContent.put("image_url", imageUrl);
                            contentItems.add(imageContent);
                        } else {
                            log.warn("Failed to process image");
                        }
                    } catch (Exception e) {
                        log.error("Error processing image", e);
                    }
                }
                
                apiUserMessage.put("content", contentItems);
                apiMessages.add(apiUserMessage);
                
                requestBody.put("messages", apiMessages);
                
                // Send API request
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
                
                // Log request (without full base64 data)
                try {
                    String debugRequestBody = objectMapper.writeValueAsString(requestBody);
                    
                    if (debugRequestBody.contains("\"url\":\"data:image/jpeg;base64,")) {
                        debugRequestBody = debugRequestBody.replaceAll(
                            "(\"url\":\"data:image/jpeg;base64,)[^\"]+", 
                            "$1...[BASE64_DATA_LENGTH: " + 
                            debugRequestBody.split("\"url\":\"data:image/jpeg;base64,")[1].split("\"")[0].length() + 
                            "]..."
                        );
                    }
                    
                    log.info("OpenAI API Request: {}", debugRequestBody);
                } catch (Exception e) {
                    log.warn("Failed to log request body", e);
                }
                
                ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                        "https://api.openai.com/v1/chat/completions", 
                        request, 
                        String.class);
                
                log.info("OpenAI API Response Status: {}", responseEntity.getStatusCode());
                
                // Parse response
                Map<String, Object> responseMap = objectMapper.readValue(responseEntity.getBody(), Map.class);
                
                // Check for errors
                if (responseMap.containsKey("error")) {
                    Map<String, Object> error = (Map<String, Object>) responseMap.get("error");
                    String errorMessage = (String) error.get("message");
                    log.error("OpenAI API Error: {}", errorMessage);
                    return VerificationDto.bothFailure("OpenAI API Error: " + errorMessage);
                }
                
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                Map<String, Object> firstChoice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                String responseContent = (String) message.get("content");
                
                return parseVerificationResponse(responseContent);
            } else {
                // For text-only requests, we can use the OpenAI client
                ChatCompletionRequest request = ChatCompletionRequest.builder()
                        .model(MODEL)
                        .messages(messages)
                        .maxTokens(500)
                        .build();
                
                String response = openAiService.createChatCompletion(request)
                        .getChoices().get(0).getMessage().getContent();
                
                return parseVerificationResponse(response);
            }
        } catch (Exception e) {
            log.error("Error verifying text and images with OpenAI", e);
            return VerificationDto.bothFailure("Failed to verify content: " + e.getMessage());
        }
    }
    
    /**
     * Encodes a MultipartFile to base64 string
     */
    private String encodeMultipartFileToBase64(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.error("Empty or null file");
            return null;
        }
        
        try {
            byte[] fileBytes = file.getBytes();
            if (fileBytes.length == 0) {
                log.error("File contains no data");
                return null;
            }
            
            // Check file size (20MB limit)
            if (fileBytes.length > 20 * 1024 * 1024) {
                log.error("File too large: {} bytes", fileBytes.length);
                return null;
            }
            
            String base64 = Base64.getEncoder().encodeToString(fileBytes);
            log.info("Successfully encoded image, size: {} bytes, base64 length: {}", 
                    fileBytes.length, base64.length());
            return base64;
        } catch (IOException e) {
            log.error("Failed to read or encode file", e);
            return null;
        }
    }
    
    private VerificationDto parseVerificationResponse(String response) {
        boolean textResult = false;
        boolean imageResult = false;
        String successReason = null;
        String failureReason = null;
        
        // Extract text_result
        Pattern textPattern = Pattern.compile("text_result\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE);
        Matcher textMatcher = textPattern.matcher(response);
        if (textMatcher.find()) {
            textResult = "true".equalsIgnoreCase(textMatcher.group(1));
            log.info("Text result: {}", textResult);
        }
        
        // Extract image_result
        Pattern imagePattern = Pattern.compile("image_result\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE);
        Matcher imageMatcher = imagePattern.matcher(response);
        if (imageMatcher.find()) {
            imageResult = "true".equalsIgnoreCase(imageMatcher.group(1));
            log.info("Image result: {}", imageResult);
        }
        
        // Extract success_reason
        Pattern successPattern = Pattern.compile("success_reason\\s*:\\s*(.+?)(?=\\n|$)", Pattern.CASE_INSENSITIVE);
        Matcher successMatcher = successPattern.matcher(response);
        if (successMatcher.find()) {
            successReason = successMatcher.group(1).trim();
            log.info("Success reason: {}", successReason);
        }
        
        // Extract failure_reason
        Pattern failurePattern = Pattern.compile("failure_reason\\s*:\\s*(.+?)(?=\\n|$)", Pattern.CASE_INSENSITIVE);
        Matcher failureMatcher = failurePattern.matcher(response);
        if (failureMatcher.find()) {
            failureReason = failureMatcher.group(1).trim();
            log.info("Failure reason: {}", failureReason);
        }
        
        return VerificationDto.builder()
                .textResult(textResult)
                .imageResult(imageResult)
                .successReason(successReason)
                .failureReason(failureReason)
                .build();
    }
} 
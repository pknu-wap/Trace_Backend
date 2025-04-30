package com.example.trace.gpt.service;

import com.example.trace.gpt.domain.Verification;
import com.example.trace.gpt.dto.PostVerificationResult;
import com.example.trace.post.domain.Post;
import com.example.trace.post.domain.PostImage;
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
import java.net.URL;
import java.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostVerificationServiceImpl implements PostVerificationService {

    private final OpenAiService openAiService;
    private static final String MODEL = "gpt-4o";
    
    @Value("${openai.api.key}")
    private String openaiApiKey;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PostVerificationResult verifyPost(Post post) {
        String content = post.getContent();
        List<PostImage> images = post.getImages();
        
        if (images == null || images.isEmpty()) {
            // Only text verification is needed
            PostVerificationResult result = verifyTextOnly(content);

            if (result.isTextResult()) {
                Verification.builder()
                        .post(post)
                        .isTextVerified(true)
                        .successReason(result.getSuccessReason())
                        .build();
                return result;

            } else {
                Verification.builder()
                        .post(post)
                        .isTextVerified(false)
                        .failureReason(result.getFailureReason())
                        .build();
                return result;
            }

        } else {
            // Both text and image verification is needed
            PostVerificationResult result = verifyTextAndImages(content, images);
            if(result.isTextResult()&& result.isImageResult()){
                Verification.builder()
                        .post(post)
                        .isTextVerified(true)
                        .isImageVerified(true)
                        .successReason(result.getSuccessReason())
                        .build();
                return result;
            } else if (result.isTextResult()&& !result.isImageResult()) {
                Verification.builder()
                        .post(post)
                        .isTextVerified(true)
                        .isImageVerified(false)
                        .failureReason(result.getFailureReason())
                        .build();
                return result;
            }
            else if (!result.isTextResult()&& result.isImageResult()){
                // 이미지 결과를 강제로 false로 조정
                PostVerificationResult correctedResult = PostVerificationResult.builder()
                        .textResult(false)
                        .imageResult(false)
                        .failureReason(result.getFailureReason() != null ?
                                result.getFailureReason() : "gpt의 논리적 오류")
                        .build();

                Verification.builder()
                        .post(post)
                        .isTextVerified(false)
                        .isImageVerified(false)
                        .failureReason(correctedResult.getFailureReason())
                        .build();
                return correctedResult;
            }
            else {
                Verification.builder()
                        .post(post)
                        .isTextVerified(false)
                        .isImageVerified(false)
                        .failureReason(result.getFailureReason())
                        .build();
                return result;
            }
        }
    }
    
    private PostVerificationResult verifyTextOnly(String content) {
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
            return PostVerificationResult.textOnlyFailure("Failed to verify text: " + e.getMessage());
        }
    }
    
    private PostVerificationResult verifyTextAndImages(String content, List<PostImage> images) {
        try {
            // 직접 OpenAI API 호출하기 위한 요청 구성
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + openaiApiKey);
            
            // API 요청 본문 구성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", MODEL);
            requestBody.put("max_tokens", 500);
            
            // 메시지 배열 구성
            List<Map<String, Object>> messages = new ArrayList<>();
            
            // 시스템 메시지 추가
            Map<String, Object> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are an AI assistant tasked with verifying if the given text describes an act of kindness " +
                    "and if the provided images properly describe the text and relate to acts of kindness. " +
                    "Respond in the following format exactly:\n" +
                    "text_result: true/false\n" +
                    "image_result: true/false\n" +
                    "success_reason: [reason for success, only if any result is true]\n" +
                    "failure_reason: [reason for failure, only if any result is false]");
            messages.add(systemMessage);
            
            // 유저 메시지 추가
            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            
            // 유저 메시지의 콘텐츠 구성 (배열)
            List<Map<String, Object>> contentItems = new ArrayList<>();
            
            // 텍스트 콘텐츠 추가
            Map<String, Object> textContent = new HashMap<>();
            textContent.put("type", "text");
            textContent.put("text", "Please verify the following:\n\n" +
                    "1. Does the text describe an act of kindness?\n" +
                    "2. Do the images properly describe the text and relate to acts of kindness?\n\n" +
                    "Text: " + content);
            contentItems.add(textContent);
            
            // 이미지 추가
            for (PostImage image : images) {
                try {
                    String base64Image = downloadAndEncodeImage(image.getImageUrl());
                    if (base64Image != null) {
                        Map<String, Object> imageContent = new HashMap<>();
                        imageContent.put("type", "image_url");
                        
                        Map<String, String> imageUrl = new HashMap<>();
                        imageUrl.put("url", "data:image/jpeg;base64," + base64Image);
                        log.info("Image URL length: {}", base64Image.length());
                        
                        imageContent.put("image_url", imageUrl);
                        contentItems.add(imageContent);
                    } else {
                        log.warn("Failed to process image: " + image.getImageUrl());
                    }
                } catch (Exception e) {
                    log.error("Error processing image: " + image.getImageUrl(), e);
                }
            }
            
            userMessage.put("content", contentItems);
            messages.add(userMessage);
            
            requestBody.put("messages", messages);
            
            // API 요청 보내기
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // 디버깅을 위해 요청 내용 로깅 (이미지 데이터는 길이만 로깅)
            try {
                String debugRequestBody = objectMapper.writeValueAsString(requestBody);
                
                // 실제 base64 이미지 데이터는 너무 길어서 길이만 로깅
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
            
            // 응답 로깅
            log.info("OpenAI API Response Status: {}", responseEntity.getStatusCode());
            
            // 응답 파싱 
            Map<String, Object> responseMap = objectMapper.readValue(responseEntity.getBody(), Map.class);
            
            // 에러 체크
            if (responseMap.containsKey("error")) {
                Map<String, Object> error = (Map<String, Object>) responseMap.get("error");
                String errorMessage = (String) error.get("message");
                log.error("OpenAI API Error: {}", errorMessage);
                return PostVerificationResult.bothFailure("OpenAI API Error: " + errorMessage);
            }
            
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            String responseContent = (String) message.get("content");
            
            return parseVerificationResponse(responseContent);
            
        } catch (Exception e) {
            log.error("Error verifying text and images with OpenAI", e);
            return PostVerificationResult.bothFailure("Failed to verify content: " + e.getMessage());
        }
    }
    
    /**
     * Downloads an image from a URL and encodes it as base64
     */
    private String downloadAndEncodeImage(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            log.error("Empty or null image URL");
            return null;
        }
        
        // URL 형식 검증
        if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
            log.error("Invalid image URL format: {}", imageUrl);
            return null;
        }
        
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setConnectTimeout(10000); // 10초 타임아웃
            connection.setReadTimeout(10000);  // 10초 타임아웃
            connection.connect();
            
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                log.error("Failed to connect to image URL: " + imageUrl + ", status: " + connection.getResponseCode());
                return null;
            }
            
            // 컨텐츠 타입 확인 (이미지인지)
            String contentType = connection.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                log.error("URL does not point to an image. Content-Type: {}", contentType);
                return null;
            }
            
            // 이미지 크기 확인 (너무 크면 실패할 수 있음)
            int contentLength = connection.getContentLength();
            if (contentLength > 20 * 1024 * 1024) { // 20MB 제한
                log.error("Image too large: {} bytes", contentLength);
                return null;
            }
            
            try (InputStream in = connection.getInputStream()) {
                // 바이트 배열로 직접 읽기
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                
                byte[] imageBytes = out.toByteArray();
                if (imageBytes.length == 0) {
                    log.error("Downloaded empty image from URL: " + imageUrl);
                    return null;
                }
                
                // 이미지 크기가 적절한지 확인 (너무 크면 API 요청이 실패할 수 있음)
                if (imageBytes.length > 20 * 1024 * 1024) { // 20MB 제한
                    log.error("Image too large after download: {} bytes", imageBytes.length);
                    return null;
                }
                
                String base64 = Base64.getEncoder().encodeToString(imageBytes);
                log.info("Successfully encoded image from URL: {}, size: {} bytes, base64 length: {}", 
                        imageUrl, imageBytes.length, base64.length());
                return base64;
            }
        } catch (IOException e) {
            log.error("Failed to download or encode image: " + imageUrl, e);
            return null;
        }
    }
    
    private PostVerificationResult parseVerificationResponse(String response) {
        boolean textResult = false;
        boolean imageResult = false;
        String successReason = null;
        String failureReason = null;
        
        // Extract text_result
        Pattern textPattern = Pattern.compile("text_result\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE);
        Matcher textMatcher = textPattern.matcher(response);
        if (textMatcher.find()) {
            textResult = "true".equalsIgnoreCase(textMatcher.group(1));
        }
        
        // Extract image_result
        Pattern imagePattern = Pattern.compile("image_result\\s*:\\s*(true|false)", Pattern.CASE_INSENSITIVE);
        Matcher imageMatcher = imagePattern.matcher(response);
        if (imageMatcher.find()) {
            imageResult = "true".equalsIgnoreCase(imageMatcher.group(1));
        }
        
        // Extract success_reason
        Pattern successPattern = Pattern.compile("success_reason\\s*:\\s*(.+?)(?=\\n|$)", Pattern.CASE_INSENSITIVE);
        Matcher successMatcher = successPattern.matcher(response);
        if (successMatcher.find()) {
            successReason = successMatcher.group(1).trim();
        }
        
        // Extract failure_reason
        Pattern failurePattern = Pattern.compile("failure_reason\\s*:\\s*(.+?)(?=\\n|$)", Pattern.CASE_INSENSITIVE);
        Matcher failureMatcher = failurePattern.matcher(response);
        if (failureMatcher.find()) {
            failureReason = failureMatcher.group(1).trim();
        }
        
        return PostVerificationResult.builder()
                .textResult(textResult)
                .imageResult(imageResult)
                .successReason(successReason)
                .failureReason(failureReason)
                .build();
    }
} 
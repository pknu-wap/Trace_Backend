package com.example.trace.post.service;

import com.example.trace.global.errorcode.PostErrorCode;
import com.example.trace.global.exception.PostException;
import com.example.trace.gpt.dto.PostVerificationResult;
import com.example.trace.gpt.service.PostVerificationService;
import com.example.trace.post.domain.PostType;
import com.example.trace.user.User;
import com.example.trace.file.FileType;
import com.example.trace.file.S3UploadService;
import com.example.trace.post.dto.PostUpdateDto;
import com.example.trace.post.domain.Post;
import com.example.trace.post.domain.PostImage;
import com.example.trace.post.dto.PostCreateDto;
import com.example.trace.post.dto.PostDto;
import com.example.trace.auth.repository.UserRepository;
import com.example.trace.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;
    private final PostVerificationService postVerificationService;

    private static final int MAX_IMAGES = 5;

    @Override
    @Transactional
    public PostDto createPost(PostCreateDto postCreateDto,Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new PostException(PostErrorCode.USER_NOT_FOUND));

        Post post = Post.builder()
                .title(postCreateDto.getTitle())
                .content(postCreateDto.getContent())
                .user(user)
                .build();

        Post savedPost = postRepository.save(post);
        return PostDto.fromEntity(savedPost);
    }


    @Override
    @Transactional
    public PostDto createPostWithPictures(PostCreateDto postCreateDto, String ProviderId) {
        User user = userRepository.findByProviderId(ProviderId)
                .orElseThrow(() -> new PostException(PostErrorCode.USER_NOT_FOUND));

        if (postCreateDto.getContent() == null || postCreateDto.getContent().isEmpty()) {
            throw new PostException(PostErrorCode.CONTENT_EMPTY);
        }


        Post post = Post.builder()
                .postType(PostType.valueOf(postCreateDto.getPostType()))
                .viewCount(0L)
                .title(postCreateDto.getTitle())
                .content(postCreateDto.getContent())
                .user(user)
                .build();

        Post savedPost = postRepository.save(post);

        List<MultipartFile> imageFiles = postCreateDto.getImageFiles();
        if (imageFiles != null && !imageFiles.isEmpty()) {
            int imagesToProcess = Math.min(imageFiles.size(), MAX_IMAGES);
            
            for (int i = 0; i < imagesToProcess; i++) {
                MultipartFile file = imageFiles.get(i);
                log.info("Processing image file: {}", file.getOriginalFilename());
                try {
                    String imageUrl = s3UploadService.saveFile(file, FileType.POST, ProviderId);
                    PostImage postImage = PostImage.builder()
                            .post(savedPost)
                            .imageUrl(imageUrl)
                            .order(i)
                            .build();
                    savedPost.addImage(postImage);
                } catch (Exception e) {
                    throw new PostException(PostErrorCode.POST_IMAGE_UPLOAD_FAILED);
                }
            }
        }
        
        return PostDto.fromEntity(savedPost);
    }

    @Override
    @Transactional
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
        post.incrementViewCount();
        return PostDto.fromEntity(post);
    }

    @Override
    @Transactional
    public PostDto updatePost(Long id, PostUpdateDto postUpdateDto, String providerId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
        
        if (!post.getUser().getProviderId().equals(providerId)) {
            throw new PostException(PostErrorCode.POST_UPDATE_FORBIDDEN);
        }
        
        post.editPost(postUpdateDto.getTitle(),postUpdateDto.getContent());

        Post updatedPost = postRepository.save(post);
        return PostDto.fromEntity(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(Long id, String providerId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
        
        if (!post.getUser().getProviderId().equals(providerId)) {
            throw new PostException(PostErrorCode.POST_DELETE_FORBIDDEN);
        }
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public PostVerificationResult verifyPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));

        return postVerificationService.verifyPost(post);
    }

}
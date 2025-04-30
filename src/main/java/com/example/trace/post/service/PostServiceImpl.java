package com.example.trace.post.service;

import com.example.trace.gpt.dto.PostVerificationResult;
import com.example.trace.gpt.service.PostVerificationService;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
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
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

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
    public PostDto createPostWithPictures(PostCreateDto postCreateDto, Long userId, String ProviderId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Post post = Post.builder()
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
                try {
                    String imageUrl = s3UploadService.saveFile(file, FileType.POST, ProviderId);
                    
                    PostImage postImage = PostImage.builder()
                            .post(savedPost)
                            .imageUrl(imageUrl)
                            .order(i)
                            .build();
                    savedPost.addImage(postImage);
                } catch (Exception e) {
                    throw new RuntimeException("파일 업로드에 실패했습니다: " + e.getMessage());
                }
            }
        }
        
        return PostDto.fromEntity(savedPost);
    }

    @Override
    @Transactional
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        return PostDto.fromEntity(post);
    }

    @Override
    @Transactional
    public PostDto updatePost(Long id, PostUpdateDto postUpdateDto, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        if (!post.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("게시글을 수정할 권한이 없습니다.");
        }
        
        post.setTitle(postUpdateDto.getTitle());
        post.setContent(postUpdateDto.getContent());

        Post updatedPost = postRepository.save(post);
        return PostDto.fromEntity(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(Long id, Long userId) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        if (!post.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("게시글을 삭제할 권한이 없습니다.");
        }
        
        postRepository.delete(post);
    }

    @Override
    @Transactional(readOnly = true)
    public PostVerificationResult verifyPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        return postVerificationService.verifyPost(post);
    }

} 
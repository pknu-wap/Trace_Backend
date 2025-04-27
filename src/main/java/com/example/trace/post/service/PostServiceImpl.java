package com.example.trace.post.service;

import com.example.trace.auth.domain.User;
import com.example.trace.file.FileType;
import com.example.trace.file.S3UploadService;
import com.example.trace.post.dto.PostUpdateDto;
import com.example.trace.post.domain.Post;
import com.example.trace.post.dto.PostCreateDto;
import com.example.trace.post.dto.PostDto;
import com.example.trace.auth.repository.UserRepository;
import com.example.trace.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;

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
    public PostDto createPostWithPictures(PostCreateDto postCreateDto,Long userId,String ProviderId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        if(postCreateDto.getImageFile() != null) {
            try{
                postCreateDto.setImageUrl(s3UploadService.saveFile(postCreateDto.getImageFile(), FileType.POST,ProviderId));
            }
            catch (Exception e) {
                throw new RuntimeException("파일 업로드에 실패했습니다.");
            }
        }

        Post post = Post.builder()
                .title(postCreateDto.getTitle())
                .content(postCreateDto.getContent())
                .imageUrl(postCreateDto.getImageUrl())
                .user(user)
                .build();

        Post savedPost = postRepository.save(post);
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

} 
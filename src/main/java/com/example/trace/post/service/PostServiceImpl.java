package com.example.trace.post.service;

import com.example.trace.emotion.EmotionService;
import com.example.trace.emotion.EmotionType;
import com.example.trace.emotion.dto.EmotionCountDto;
import com.example.trace.global.errorcode.PostErrorCode;
import com.example.trace.global.exception.PostException;
import com.example.trace.gpt.domain.Verification;
import com.example.trace.gpt.dto.VerificationDto;
import com.example.trace.gpt.service.PostVerificationService;
import com.example.trace.post.domain.PostType;
import com.example.trace.post.dto.cursor.CursorResponse;
import com.example.trace.post.dto.cursor.PostCursorRequest;
import com.example.trace.post.dto.post.PostFeedDto;
import com.example.trace.user.User;
import com.example.trace.file.FileType;
import com.example.trace.file.S3UploadService;
import com.example.trace.post.dto.post.PostUpdateDto;
import com.example.trace.post.domain.Post;
import com.example.trace.post.domain.PostImage;
import com.example.trace.post.dto.post.PostCreateDto;
import com.example.trace.post.dto.post.PostDto;
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
    private final EmotionService emotionService;

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
    public PostDto createPost(PostCreateDto postCreateDto, String ProviderId,VerificationDto verificationDto) {
        User user = userRepository.findByProviderId(ProviderId)
                .orElseThrow(() -> new PostException(PostErrorCode.USER_NOT_FOUND));

        if (postCreateDto.getContent() == null || postCreateDto.getContent().isEmpty()) {
            throw new PostException(PostErrorCode.CONTENT_EMPTY);
        }

        if (postCreateDto.getTitle() == null || postCreateDto.getTitle().isEmpty()) {
            throw new PostException(PostErrorCode.TITLE_EMPTY);
        }

        Verification verification = null;
        if(verificationDto != null){
            verification = postVerificationService.makeVerification(verificationDto);
        }

        Post post = Post.builder()
                .postType(postCreateDto.getPostType())
                .viewCount(0L)
                .title(postCreateDto.getTitle())
                .content(postCreateDto.getContent())
                .user(user)
                .verification(verification)
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
    public PostDto getPostById (Long postId, User user){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));

        post.incrementViewCount();

        EmotionCountDto emotionCountDto = emotionService.getEmotionCountsByType(postId);

        EmotionType yourEmotionType = emotionService.getYourEmotion(postId,user);

        PostDto postDto = PostDto.fromEntity(post);

        postDto.setEmotionCount(emotionCountDto);
        postDto.setOwner(post.getUser().equals(user));
        postDto.setYourEmotionType(yourEmotionType);

        return postDto;
    }


    @Transactional(readOnly = true)
    public CursorResponse<PostFeedDto> getAllPostsWithCursor(PostCursorRequest request, String providerId) {
        // 커서 요청 처리
        int size = request.getSize() != null ? request.getSize() : 10;

        String postType = null;
        if (request.getPostType() != null && !request.getPostType().isEmpty()) {
            try {
                postType = request.getPostType();
            } catch (IllegalArgumentException e) {
                throw new PostException(PostErrorCode.INVALID_POST_TYPE);
            }
        }

        // 게시글 조회
        List<PostFeedDto> posts;
        if (request.getCursorDateTime() == null || request.getCursorId() == null) {
            // 첫 페이지 조회
            posts = postRepository.findPostsWithCursor(null,null, size + 1,postType);
        } else {
            // 다음 페이지 조회
            posts = postRepository.findPostsWithCursor(
                    request.getCursorDateTime(), request.getCursorId(), size + 1, postType );
        }


        // 다음 페이지 여부 확인
        boolean hasNext = false;
        if (posts.size() > size) {
            hasNext = true;
            posts = posts.subList(0, size);
        }

        // 커서 메타데이터 생성
        CursorResponse.CursorMeta nextCursor = null;
        if (!posts.isEmpty() && hasNext) {
            PostFeedDto lastPost = posts.get(posts.size() - 1);
            nextCursor = CursorResponse.CursorMeta.builder()
                    .dateTime(lastPost.getCreatedAt())
                    .id(lastPost.getPostId())
                    .build();
        }

        // 응답 생성
        return CursorResponse.<PostFeedDto>builder()
                .content(posts)
                .hasNext(hasNext)
                .cursor(nextCursor)
                .build();
    }

    @Override
    @Transactional
    public PostDto updatePost (Long id, PostUpdateDto postUpdateDto, String providerId){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));

        if (!post.getUser().getProviderId().equals(providerId)) {
            throw new PostException(PostErrorCode.POST_UPDATE_FORBIDDEN);
        }

        post.editPost(postUpdateDto.getTitle(), postUpdateDto.getContent());

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



}
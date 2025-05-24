package com.example.trace.post.service;

import com.example.trace.auth.repository.UserRepository;
import com.example.trace.global.errorcode.PostErrorCode;
import com.example.trace.global.exception.PostException;
import com.example.trace.post.domain.Comment;
import com.example.trace.post.domain.Post;
import com.example.trace.post.dto.comment.CommentCreateDto;
import com.example.trace.post.dto.comment.CommentDto;
import com.example.trace.post.repository.CommentRepository;
import com.example.trace.post.repository.PostRepository;
import com.example.trace.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    public CommentDto addComment(Long postId ,CommentCreateDto commentCreateDto, String providerId) {

        Post postToAddComment = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));

        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (commentCreateDto.getContent() == null || commentCreateDto.getContent().isEmpty()) {
            throw new PostException(PostErrorCode.CONTENT_EMPTY);
        }

        Comment comment = Comment.builder()
                .post(postToAddComment)
                .content(commentCreateDto.getContent())
                .user(user)
                .build();
        commentRepository.save(comment);

        postToAddComment.addComment(comment);

        return CommentDto.fromEntity(comment);
    }

    public void deleteComment(Long commentId, String ProviderId) {
        User user = userRepository.findByProviderId(ProviderId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new PostException(PostErrorCode.COMMENT_NOT_FOUND));
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new PostException(PostErrorCode.COMMENT_DELETE_FORBIDDEN);
        }
        commentRepository.delete(comment);
    }

    public CommentDto addChildrenComment(Long postId,Long commentId,CommentCreateDto commentCreateDto, String ProviderId){
        User user = userRepository.findByProviderId(ProviderId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new PostException(PostErrorCode.COMMENT_NOT_FOUND));

        if (parentComment.getPost() == null) {
            throw new PostException(PostErrorCode.POST_NOT_FOUND);
        }

        Post postToAddComment = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));

        if (commentCreateDto.getContent() == null || commentCreateDto.getContent().isEmpty()) {
            throw new PostException(PostErrorCode.CONTENT_EMPTY);
        }

        Comment childrenComment = Comment.builder()
                .post(postToAddComment)
                .content(commentCreateDto.getContent())
                .user(user)
                .parent(parentComment)
                .build();
        commentRepository.save(childrenComment);

        parentComment.addChild(childrenComment);

        return CommentDto.fromEntity(childrenComment);
    }

}

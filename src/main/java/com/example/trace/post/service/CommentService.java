package com.example.trace.post.service;

import com.example.trace.global.errorcode.PostErrorCode;
import com.example.trace.global.exception.PostException;
import com.example.trace.global.fcm.NotifiacationEventService;
import com.example.trace.post.domain.Comment;
import com.example.trace.post.domain.Post;
import com.example.trace.post.domain.PostType;
import com.example.trace.post.dto.comment.CommentCreateDto;
import com.example.trace.post.dto.comment.CommentDto;
import com.example.trace.post.dto.cursor.CommentCursorRequest;
import com.example.trace.post.dto.cursor.CursorResponse;
import com.example.trace.post.repository.CommentRepository;
import com.example.trace.post.repository.PostRepository;
import com.example.trace.user.User;
import com.example.trace.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    private final NotifiacationEventService notifiacationEventService;



    public CommentDto addComment(Long postId ,CommentCreateDto commentCreateDto, String providerId) {

        Post postToAddComment = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));

        User commentAuthorUser = userService.getUser(providerId);

        if (commentCreateDto.getContent() == null || commentCreateDto.getContent().isEmpty()) {
            throw new PostException(PostErrorCode.CONTENT_EMPTY);
        }


        Comment comment = Comment.builder()
                .post(postToAddComment)
                .content(commentCreateDto.getContent())
                .user(commentAuthorUser)
                .isDeleted(false)
                .build();
        commentRepository.save(comment);

        postToAddComment.addComment(comment);

        String postAuthorProviderId = postToAddComment.getUser().getProviderId();
        String commentAuthorUserProviderId = commentAuthorUser.getProviderId();

        // 게시글 작성자가 댓글을 단게 아니라면 게시글 작성자에게 알림
        if(postAuthorProviderId.equals(commentAuthorUserProviderId)){
            PostType postType = postToAddComment.getPostType();
            String commentComment = comment.getContent();
            notifiacationEventService.sendCommentNotification(postAuthorProviderId,postId,postType,commentComment);
        }

        return CommentDto.fromEntity(comment,providerId);
    }

    @Transactional
    public CommentDto deleteComment(Long commentId, String providerId) {
        User user = userService.getUser(providerId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new PostException(PostErrorCode.COMMENT_NOT_FOUND));
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new PostException(PostErrorCode.COMMENT_DELETE_FORBIDDEN);
        }
        comment.removeSelf();
        return CommentDto.fromEntity(comment,providerId);
    }

    public CommentDto addChildrenComment(Long postId,Long commentId,CommentCreateDto commentCreateDto, String providerId){
        User user = userService.getUser(providerId);

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

        return CommentDto.fromEntity(childrenComment,providerId);
    }

    public CursorResponse<CommentDto> getCommentsWithCursor(CommentCursorRequest request,Long postId, String providerId) {
        User user = userService.getUser(providerId);

        // 커서 기반 부모 댓글의 id 리스트 가져오기
        List<Long> parentCommentsIdList;
        if (request.getCursorDateTime() == null || request.getCursorId() == null) {
            // 첫 페이지 조회
            parentCommentsIdList = commentRepository.findParentCommentsIdWithCursor(null, null, postId,request.getSize() + 1,providerId);
        } else {
            // 다음 페이지 조회
            parentCommentsIdList = commentRepository.findParentCommentsIdWithCursor(
                    request.getCursorDateTime(), request.getCursorId(),  postId,request.getSize() + 1,providerId);
        }

        // 다음 페이지 여부 확인
        boolean hasNext = false;
        if (parentCommentsIdList.size() > request.getSize()) {
            hasNext = true;
            parentCommentsIdList = parentCommentsIdList.subList(0, request.getSize());
        }

        // 댓글 + 답글 들고오기
        List<CommentDto> comments = commentRepository.findComments(parentCommentsIdList, providerId);

        List<CommentDto> hierarchicalComments = buildHierarchicalComments(comments);

        CursorResponse.CursorMeta nextCursor = null;
        if (!hierarchicalComments.isEmpty() && hasNext) {
            CommentDto lastComment = hierarchicalComments.get(hierarchicalComments.size() - 1);
            nextCursor = CursorResponse.CursorMeta.builder()
                    .dateTime(lastComment.getCreatedAt())
                    .id(lastComment.getCommentId())
                    .build();
        }

        return CursorResponse.<CommentDto>builder()
                .content(hierarchicalComments)
                .hasNext(hasNext)
                .cursor(nextCursor)
                .build();
    }

    private List<CommentDto> buildHierarchicalComments(List<CommentDto> flatComments) {
        Map<Long, CommentDto> commentMap = new HashMap<>();
        List<CommentDto> parentComments = new ArrayList<>();

        // 1단계: 모든 댓글을 Map에 저장하고 부모 댓글 분리
        for (CommentDto comment : flatComments) {
            commentMap.put(comment.getCommentId(), comment);
            if (comment.isParent()) {
                parentComments.add(comment);
            }
        }

        // 2단계: 자식 댓글을 부모에 연결
        for (CommentDto comment : flatComments) {
            if (!comment.isParent()) {
                CommentDto parent = commentMap.get(comment.getParentId());
                if (parent != null) {
                    parent.addChild(comment);
                }
            }
        }

        // 3단계: 부모 댓글들을 생성 시간 순으로 정렬
        parentComments.sort((c1, c2) -> c1.getCreatedAt().compareTo(c2.getCreatedAt()));

        // 4단계: 각 부모의 자식 댓글들도 정렬
        for (CommentDto parent : parentComments) {
            parent.getChildren().sort((c1, c2) -> c1.getCreatedAt().compareTo(c2.getCreatedAt()));
        }

        return parentComments;
    }


}

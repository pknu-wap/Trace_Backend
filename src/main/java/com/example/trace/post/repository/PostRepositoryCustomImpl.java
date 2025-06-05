package com.example.trace.post.repository;

import com.example.trace.post.domain.PostType;
import com.example.trace.post.domain.QComment;
import com.example.trace.post.domain.QPost;
import com.example.trace.post.domain.QPostImage;
import com.example.trace.post.domain.cursor.SearchType;
import com.example.trace.post.dto.comment.CommentDto;
import com.example.trace.post.dto.post.PostFeedDto;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.trace.emotion.QEmotion.emotion;
import static com.example.trace.post.domain.QComment.comment;
import static com.example.trace.post.domain.QPost.post;
import static com.example.trace.post.domain.QPostImage.postImage;

@Slf4j
public class PostRepositoryCustomImpl implements PostRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    // 생성자 주입
    public PostRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    private BooleanExpression postTypeEq(PostType postType) {
        if(postType == null) return Expressions.TRUE;
        String stringPostType = postType.name();
        return post.postType.stringValue().eq(stringPostType);
    }

    StringExpression imageUrlExpr = Expressions.cases()
            .when(post.images.isEmpty()).then("")
            .otherwise(
                    JPAExpressions
                            .select(postImage.imageUrl)
                            .from(postImage)
                            .where(
                                    postImage.post.eq(post)
                                            .and(postImage.id.eq(
                                                    JPAExpressions
                                                            .select(postImage.id.min())
                                                            .from(postImage)
                                                            .where(postImage.post.eq(post))
                                            ))
                            )
            );
    Expression<Long> totalEmotionCount = JPAExpressions
            .select(emotion.count())
            .from(emotion)
            .where(emotion.post.eq(post));

    BooleanExpression isVerifiedExpr = Expressions.cases()
            .when(post.verification.isNull())
            .then(false)
            .otherwise(
                    Expressions.booleanOperation(
                            com.querydsl.core.types.Ops.OR,
                            post.verification.isImageVerified,
                            post.verification.isTextVerified
                    )
            );

    private BooleanExpression isOwnerExpr(String providerId){
        BooleanExpression isOwnerExpr = Expressions.cases()
                .when(post.user.providerId.eq(providerId))
                .then(true)
                .otherwise(false);
        return isOwnerExpr;
    }



    private BooleanExpression postCursorCondition(LocalDateTime cursorDateTime, Long cursorId) {
        if (cursorDateTime == null || cursorId == null) {
            return Expressions.TRUE; // 첫 페이지
        }
        // 커서 이전의 데이터를 가져오는 조건
        return post.createdAt.lt(cursorDateTime)
                .or(post.createdAt.eq(cursorDateTime).and(post.id.lt(cursorId)));
    }

    private BooleanExpression commentCursorCondition(LocalDateTime cursorDateTime, Long cursorId) {
        if (cursorDateTime == null || cursorId == null) {
            return Expressions.TRUE; // 첫 페이지
        }
        // 커서 이전의 데이터를 가져오는 조건
        return comment.createdAt.gt(cursorDateTime)
                .or(comment.createdAt.eq(cursorDateTime).and(comment.id.gt(cursorId)));
    }

    private BooleanExpression searchCondition(String keyword, SearchType searchType) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Expressions.TRUE;
        }

        String trimmedKeyword = keyword.trim();

        switch (searchType) {
            case TITLE:
                return post.title.containsIgnoreCase(trimmedKeyword);
            case CONTENT:
                return post.content.containsIgnoreCase(trimmedKeyword);
            case ALL:
            default:
                return post.title.containsIgnoreCase(trimmedKeyword)
                        .or(post.content.containsIgnoreCase(trimmedKeyword));
        }
    }


    @Override
    public List<PostFeedDto> findPostsWithCursor(
            LocalDateTime cursorDateTime,
            Long cursorId,
            int size,
            PostType postType,
            String providerId) {

        // Q클래스 정의
        QPost post = QPost.post;
        QPostImage postImage = new QPostImage("postImage");

        Expression<Long> totalEmotionCount = JPAExpressions
                .select(emotion.count())
                .from(emotion)
                .where(emotion.post.eq(post));


        return queryFactory
                .select(Projections.constructor(PostFeedDto.class,
                        post.id.as("postId"),
                        post.postType,
                        post.title,
                        post.content,
                        post.user.providerId,
                        post.user.nickname,
                        post.user.profileImageUrl,
                        imageUrlExpr,
                        post.viewCount,
                        post.commentList.size().longValue(),
                        post.createdAt,
                        post.updatedAt,
                        isVerifiedExpr,
                        isOwnerExpr(providerId),
                        totalEmotionCount
                ))
                .from(post)
                .leftJoin(post.user)
                .leftJoin(post.verification) // verification 조인 추가
                .where(
                        postTypeEq(postType),
                        postCursorCondition(cursorDateTime, cursorId)
                )
                .orderBy(post.createdAt.desc(), post.id.desc())
                .limit(size + 1)
                .fetch();
    }

    @Override
    public List<PostFeedDto> findPostsWithCursorAndSearch(
            LocalDateTime cursorDateTime,
            Long cursorId,
            int size,
            PostType postType,
            String keyword,
            SearchType searchType,
            String providerId) {

        QPost post = QPost.post;
        QPostImage postImage = new QPostImage("postImage");

        return queryFactory
                .select(Projections.constructor(PostFeedDto.class,
                        post.id.as("postId"),
                        post.postType,
                        post.title,
                        post.content,
                        post.user.providerId,
                        post.user.nickname,
                        post.user.profileImageUrl,
                        imageUrlExpr,
                        post.viewCount,
                        post.commentList.size().longValue(),
                        post.createdAt,
                        post.updatedAt,
                        isVerifiedExpr,
                        isOwnerExpr(providerId),
                        totalEmotionCount
                ))
                .from(post)
                .leftJoin(post.user)
                .leftJoin(post.verification)
                .where(
                        postTypeEq(postType),
                        postCursorCondition(cursorDateTime, cursorId),
                        searchCondition(keyword, searchType) // 새로 추가할 검색 조건
                )
                .orderBy(post.createdAt.desc(), post.id.desc())
                .limit(size + 1)
                .fetch();
    }





    public List<Long> findParentCommentsIdWithCursor(
            LocalDateTime cursorDateTime,
            Long cursorId,
            Long postId,
            int size,
            String providerId
    ){
        QPost post = QPost.post;
        QComment comment = QComment.comment;

        List<Long> parentCommentIds = queryFactory
                .select(comment.id)
                .from(comment)
                .where(
                        comment.post.id.eq(postId),
                        comment.parent.isNull(), // 부모 댓글만
                        commentCursorCondition(cursorDateTime, cursorId)
                )
                .orderBy(comment.createdAt.asc(), comment.id.asc())
                .limit(size)
                .fetch();

        if (parentCommentIds.isEmpty()) {
            return Collections.emptyList();
        }
        for(Long id : parentCommentIds){
            log.info("부모 id {}",id);
        }
        return parentCommentIds;
    }

    @Override
    public List<CommentDto> findComments(
            List<Long> parentCommentIds,
            String providerId
    ){
        QComment comment = QComment.comment;

        List<CommentDto> allComments = queryFactory
                .select(Projections.constructor(CommentDto.class,
                        comment.post.id.as("postId"),
                        comment.user.providerId,
                        comment.id.as("commentId"),
                        comment.parent.id.as("parentId"),
                        comment.user.nickname,
                        comment.user.profileImageUrl,
                        comment.content,
                        comment.createdAt,
                        comment.user.providerId.eq(providerId).as("isOwner"),
                        comment.isDeleted.as("isDeleted")
                ))
                .from(comment)
                .leftJoin(comment.user)
                .where(
                        comment.id.in(parentCommentIds).or(comment.parent.id.in(parentCommentIds))
                )
                .orderBy(comment.parent.id.asc().nullsFirst(),
                        comment.createdAt.asc())
                .fetch();


        return allComments;
    }

    @Override
    public List<PostFeedDto> findUserPosts(
            String providerId,
            LocalDateTime cursorDateTime,
            Long cursorId,
            int size) {

        return queryFactory
                .select(Projections.constructor(PostFeedDto.class,
                        post.id.as("postId"),
                        post.postType,
                        post.title,
                        post.content,
                        post.user.providerId,
                        post.user.nickname,
                        post.user.profileImageUrl,
                        imageUrlExpr,
                        post.viewCount,
                        post.commentList.size().longValue(),
                        post.createdAt,
                        post.updatedAt,
                        isVerifiedExpr,
                        isOwnerExpr(providerId),
                        totalEmotionCount
                ))
                .from(post)
                .leftJoin(post.user)
                .leftJoin(post.verification)
                .where(
                        post.user.providerId.eq(providerId),
                        postCursorCondition(cursorDateTime, cursorId)
                )
                .orderBy(post.createdAt.desc(), post.id.desc())
                .limit(size + 1)
                .fetch();
    }

    @Override
    public List<PostFeedDto> findUserCommentedPosts(
            String providerId,
            LocalDateTime cursorDateTime,
            Long cursorId,
            int size) {

        Expression<Long> totalEmotionCount = JPAExpressions
                .select(emotion.count())
                .from(emotion)
                .where(emotion.post.eq(post));

        return queryFactory
                .select(Projections.constructor(PostFeedDto.class,
                        post.id.as("postId"),
                        post.postType,
                        post.title,
                        post.content,
                        post.user.providerId,
                        post.user.nickname,
                        post.user.profileImageUrl,
                        imageUrlExpr,
                        post.viewCount,
                        post.commentList.size().longValue(),
                        post.createdAt,
                        post.updatedAt,
                        isVerifiedExpr,
                        isOwnerExpr(providerId),
                        totalEmotionCount
                ))
                .from(post)
                .leftJoin(post.user)
                .leftJoin(post.verification)
                .where(
                    post.id.in(
                        JPAExpressions
                            .select(comment.post.id)
                            .from(comment)
                            .where(
                                comment.user.providerId.eq(providerId),
                                comment.isDeleted.eq(false)
                            )
                    ),
                    postCursorCondition(cursorDateTime, cursorId)
                )
                .orderBy(post.createdAt.desc(), post.id.desc())
                .limit(size + 1)
                .fetch();
    }

    @Override
    public List<PostFeedDto> findUserEmotedPosts(
            String providerId,
            LocalDateTime cursorDateTime,
            Long cursorId,
            int size) {

        Expression<Long> totalEmotionCount = JPAExpressions
                .select(emotion.count())
                .from(emotion)
                .where(emotion.post.eq(post));

        return queryFactory
                .select(Projections.constructor(PostFeedDto.class,
                        post.id.as("postId"),
                        post.postType,
                        post.title,
                        post.content,
                        post.user.providerId,
                        post.user.nickname,
                        post.user.profileImageUrl,
                        imageUrlExpr,
                        post.viewCount,
                        post.commentList.size().longValue(),
                        post.createdAt,
                        post.updatedAt,
                        isVerifiedExpr,
                        isOwnerExpr(providerId),
                        totalEmotionCount
                ))
                .from(post)
                .leftJoin(post.user)
                .leftJoin(post.verification)
                .where(
                    post.id.in(
                        JPAExpressions
                            .select(emotion.post.id)
                            .from(emotion)
                            .where(emotion.user.providerId.eq(providerId))
                    ),
                    postCursorCondition(cursorDateTime, cursorId)
                )
                .orderBy(post.createdAt.desc(), post.id.desc())
                .limit(size + 1)
                .fetch();
    }

}

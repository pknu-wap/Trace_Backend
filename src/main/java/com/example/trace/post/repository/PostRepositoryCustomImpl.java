package com.example.trace.post.repository;

import com.example.trace.post.domain.QPost;
import com.example.trace.post.domain.QPostImage;
import com.example.trace.post.dto.post.PostFeedDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.trace.post.domain.QPost.post;

public class PostRepositoryCustomImpl implements PostRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    // 생성자 주입
    public PostRepositoryCustomImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    private BooleanExpression postTypeEq(String postType) {
        return postType != null ? post.postType.stringValue().eq(postType) : Expressions.TRUE;
    }

    private BooleanExpression cursorCondition(LocalDateTime cursorDateTime, Long cursorId) {
        if (cursorDateTime == null || cursorId == null) {
            return Expressions.TRUE; // 첫 페이지
        }
        // 커서 이전의 데이터를 가져오는 조건
        return post.createdAt.lt(cursorDateTime)
                .or(post.createdAt.eq(cursorDateTime).and(post.id.lt(cursorId)));
    }



    @Override
    public List<PostFeedDto> findPostsWithCursor(
            LocalDateTime cursorDateTime,
            Long cursorId,
            int size,
            String postType) {

        // Q클래스 정의
        QPost post = QPost.post;
        QPostImage postImage = new QPostImage("postImage");


        // 이미지 URL 처리를 위한 case 표현식
        StringExpression imageUrlExpr = Expressions.cases()
                .when(post.images.isEmpty())
                .then("")
                .otherwise(
                        JPAExpressions
                                .select(postImage.imageUrl)
                                .from(postImage)
                                .where(postImage.post.eq(post))
                                .orderBy(postImage.id.asc())
                                .limit(1)
                );

        // verification 여부 처리를 위한 case 표현식
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

        return queryFactory
                .select(Projections.constructor(PostFeedDto.class,
                        post.id.as("postId"),
                        post.postType.stringValue(),
                        post.title,
                        post.content,
                        post.user.providerId,
                        post.user.nickname,
                        post.user.profileImageUrl,
                        imageUrlExpr, // 서브쿼리 사용
                        post.viewCount,
                        post.commentList.size().longValue(),
                        post.createdAt,
                        post.updatedAt,
                        isVerifiedExpr
                ))
                .from(post)
                .leftJoin(post.user)
                .leftJoin(post.verification) // verification 조인 추가
                .leftJoin(post.images, postImage).on(postImage.order.eq(1))
                .where(
                        postTypeEq(postType),
                        cursorCondition(cursorDateTime, cursorId) // 커서 조건
                )
                .orderBy(post.createdAt.desc(), post.id.desc())
                .limit(size + 1)
                .fetch();
    }
}

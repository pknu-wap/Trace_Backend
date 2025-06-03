package com.example.trace.global.fcm;

import com.example.trace.emotion.EmotionType;
import com.example.trace.post.domain.PostType;
import com.example.trace.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotifiacationEventService {
    private final FcmTokenNotificationService fcmTokenNotificationService;

    public void sendDailyMissionAssignedNotification(User user) {
        String providerId = user.getProviderId();
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put("type", "mission");

        fcmTokenNotificationService.sendDataOnlyMessage(
                providerId,
                "오늘의 선행 미션 도착!",
                "작은 선행으로 따뜻한 흔적을 남겨보세요!",
                additionalData
        );
    }

    public void sendCommentNotification(String providerId, Long postId, PostType postType, String commentContent) {
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put("type", "comment");
        additionalData.put("postId", String.valueOf(postId));

        fcmTokenNotificationService.sendDataOnlyMessage(
                providerId,
                postType.getType() + "게시판",
                "새로운 댓글이 달렸어요 : " + commentContent,
                additionalData
        );
    }

    public void sendEmotionNotification(
            String providerId,
            Long postId,
            PostType postType,
            EmotionType emotionType,
            String nickName) {
        Map<String, String> additionalData = new HashMap<>();
        additionalData.put("type", "emotion");
        additionalData.put("postId", String.valueOf(postId));
        additionalData.put("emotion", emotionType.name());

        fcmTokenNotificationService.sendDataOnlyMessage(
                providerId,
                postType.getType() + " 게시판",
                nickName+ "님이 당신의 흔적에 " + emotionType.getDescription() + "를 남겼어요",
                additionalData
        );
    }

}

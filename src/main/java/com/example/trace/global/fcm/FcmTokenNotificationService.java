package com.example.trace.global.fcm;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmTokenNotificationService {

    private final FirebaseMessaging firebaseMessaging;
    private final FcmTokenService fcmTokenService;

    /**
     * Data-only 메시지 전송 (notification 필드 사용 안함)
     */
    public void sendDataOnlyMessage(String providerId, String title, String body, Map<String, String> additionalData) {
        Optional<String> tokenOpt = fcmTokenService.getTokenByProviderId(providerId);

        if (tokenOpt.isEmpty()) {
            log.warn("FCM 토큰을 찾을 수 없습니다 - 사용자 ID: {}", providerId);
            return;
        }

        String token = tokenOpt.get();

        // Data-only 메시지 구성 (notification 필드 없음)
        Map<String, String> data = new HashMap<>();
        data.put("title", title);
        data.put("body", body);
        data.put("timestamp", String.valueOf(System.currentTimeMillis()));

        // 추가 데이터가 있으면 포함
        if (additionalData != null) {
            data.putAll(additionalData);
        }

        Message message = Message.builder()
                .setToken(token)
                .putAllData(data)  // notification 필드 대신 data 필드만 사용
                .build();

        log.info("fcm 알림 보내는 중..");

        try {
            String response = firebaseMessaging.send(message);
            log.info("FCM message send sucess - user ID: {}, reponse: {}", providerId, response);
        } catch (FirebaseMessagingException e) {
            handleFirebaseException(e, providerId, token);
        }
    }

    /**
     * 여러 사용자에게 동시 전송
     */
    public void sendDataOnlyMessageToMultipleUsers(List<String> providerIds, String title, String body, Map<String, String> additionalData) {
        List<String> tokens = providerIds.stream()
                .map(fcmTokenService::getTokenByProviderId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        if (tokens.isEmpty()) {
            log.warn("not found valid fcm token");
            return;
        }

        Map<String, String> data = new HashMap<>();
        data.put("title", title);
        data.put("body", body);
        data.put("timestamp", String.valueOf(System.currentTimeMillis()));

        if (additionalData != null) {
            data.putAll(additionalData);
        }

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .putAllData(data)
                .build();

        try {
            BatchResponse response = firebaseMessaging.sendMulticast(message);
            log.info("FCM 멀티캐스트 메시지 전송 완료 - 성공: {}, 실패: {}",
                    response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("FCM 멀티캐스트 메시지 전송 실패: {}", e.getMessage());
        }
    }

    private void handleFirebaseException(FirebaseMessagingException e, String providerId, String token) {
        MessagingErrorCode errorCode = e.getMessagingErrorCode();

        switch (errorCode) {
            case UNREGISTERED:
                log.warn("not valid token - provider ID: {}", providerId);
                // 토큰 삭제 로직 추가 가능
                break;
            case INVALID_ARGUMENT:
                log.error("wrong arg - provider ID: {}, error: {}", providerId, e.getMessage());
                break;
            default:
                log.error("fcm messsage send failed - provider ID: {}, error : {}, message: {}",
                        providerId, errorCode, e.getMessage());
        }
    }
}

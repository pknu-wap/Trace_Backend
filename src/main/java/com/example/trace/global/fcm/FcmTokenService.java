package com.example.trace.global.fcm;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public void saveOrUpdateToken(String providerId, String token) {
        Optional<FcmToken> existingToken = fcmTokenRepository.findByProviderId(providerId);

        if (existingToken.isPresent()) {
            FcmToken fcmToken = existingToken.get();
            fcmToken.updateToken(providerId,token);
            fcmTokenRepository.save(fcmToken);
            log.info("FCM 토큰 업데이트 완료 - 사용자 ID: {}", providerId);
        } else {
            FcmToken fcmToken = new FcmToken();
            fcmToken.updateToken(providerId,token);
            fcmTokenRepository.save(fcmToken);
            log.info("FCM 토큰 저장 완료 - 사용자 ID: {}", providerId);
        }
    }

    public Optional<String> getTokenByProviderId(String providerId) {
        return fcmTokenRepository.findByProviderId(providerId)
                .map(FcmToken::getToken);
    }
}

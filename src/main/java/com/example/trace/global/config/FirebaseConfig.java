package com.example.trace.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;



@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_SERVICE_ACCOUNT_KEY}")
    private String firebaseServiceAccountKey;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {

            // 디버깅용 로그 추가
            System.out.println("BASE64 문자열 길이: " + firebaseServiceAccountKey.length());
            System.out.println("첫 10글자: " + firebaseServiceAccountKey.substring(0, Math.min(10, firebaseServiceAccountKey.length())));

            // 공백 체크
            if (firebaseServiceAccountKey.contains(" ")) {
                throw new IllegalArgumentException("BASE64 문자열에 공백이 포함되어 있습니다");
            }

            try {
                byte[] decodedKey = Base64.getDecoder().decode(firebaseServiceAccountKey);
                InputStream credentialsStream = new ByteArrayInputStream(decodedKey);
                GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .build();

                return FirebaseApp.initializeApp(options);
            }
            catch (IllegalArgumentException e){
                System.err.println("BASE64 디코딩 실패: " + e.getMessage());
                throw e;
            }

        }
        return FirebaseApp.getInstance();
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}

package com.example.trace.client;

import com.example.trace.models.OIDCPublicKeyResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "KakaoOauthClient",
        url = "${oauth2.client.provider.kakao.jwks-uri}",
        configuration = KakaoOauthConfig.class
)
public interface KakaoOAuthClient {
    @Cacheable(value = "KakaoOauth", cacheManager = "oidcCacheManager")
    @GetMapping("")
    OIDCPublicKeyResponse getOIDCPublicKey();
}
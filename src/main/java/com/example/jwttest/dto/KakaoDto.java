package com.example.jwttest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

public class KakaoDto {

    @Getter
    public static class OAuthToken {
        private String access_token;
        private String token_type;
        private String refresh_token;
        private int expires_in;
        private String scope;
        private int refresh_token_expires_in;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter @Setter
    public static class KakaoProfile {
        private Long id;
        @JsonProperty("connected_at")
        private String connectedAt;

        @JsonProperty("kakao_account")
        private KakaoAccount kakaoAccount;

        @JsonProperty("properties")
        private ProfileProperties properties;

        @JsonIgnoreProperties(ignoreUnknown = true)
        @Getter @Setter
        public static class KakaoAccount {
            @JsonProperty("profile_nickname_needs_agreement")
            private Boolean profileNicknameNeedsAgreement;
            @JsonProperty("profile_image_needs_agreement")
            private Boolean profileImageNeedsAgreement;
            private Profile profile;

            @JsonIgnoreProperties(ignoreUnknown = true)
            @Getter @Setter
            public static class Profile {
                private String nickname;
                @JsonProperty("thumbnail_image_url")
                private String thumbnailImageUrl;
                @JsonProperty("profile_image_url")
                private String profileImageUrl;
                @JsonProperty("is_default_image")
                private Boolean isDefaultImage;
            }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        @Getter @Setter
        public static class ProfileProperties {
            private String nickname;
            @JsonProperty("profile_image")
            private String profileImage;
            @JsonProperty("thumbnail_image")
            private String thumbnailImage;
        }
    }
}

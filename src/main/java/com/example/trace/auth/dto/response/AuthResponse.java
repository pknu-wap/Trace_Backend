package com.example.trace.auth.dto.response;



public class AuthResponse {

    private final boolean isRegistered;
    private final TokenInfo tokenInfo;
    private final SignUpInfo signUpInfo;
    private AuthResponse(boolean isRegistered,TokenInfo tokenInfo, SignUpInfo signUpInfo) {
        this.isRegistered = isRegistered;
        this.tokenInfo = tokenInfo;
        this.signUpInfo = signUpInfo;
    }

    public static AuthResponse loginSuccess(String accessToken, String refreshToken) {
        TokenInfo tokenInfo = new TokenInfo(accessToken, refreshToken);
        return new AuthResponse(
                true,
                tokenInfo,
                null
        );
    }

    public static AuthResponse signupRequired(String signupToken, String providerId, String email, String nickname, String profileImage) {
        SignUpInfo signUpInfo = new SignUpInfo(signupToken, providerId, email, nickname, profileImage);
        return new AuthResponse(
                false,
                null,
                signUpInfo
        );
    }

    public static class SignUpInfo {
        private final String signupToken;
        private final String providerId;
        private final String email;
        private final String nickname;
        private final String profileImage;

        public SignUpInfo(String signupToken, String providerId, String email, String nickname, String profileImage) {
            this.signupToken = signupToken;
            this.providerId = providerId;
            this.email = email;
            this.nickname = nickname;
            this.profileImage = profileImage;
        }
    }

    public static class TokenInfo{
        private final String accessToken;
        private final String refreshToken;
        public TokenInfo(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }


}

package com.example.trace.auth.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OIDCDecodePayload {
    private String iss;
    private String aud;
    private String sub;  // user_id in Kakao
    private long iat;
    private long auth_time;
    private long exp;
    private String nonce;
    private String nickname;
    private String picture;
    private String email;
}
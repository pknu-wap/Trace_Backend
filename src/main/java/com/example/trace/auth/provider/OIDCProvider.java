package com.example.trace.auth.provider;

import com.example.trace.auth.models.OIDCDecodePayload;
import com.example.trace.auth.models.OIDCPublicKey;

public interface OIDCProvider {
    /**
     * Extract the kid from the ID token header
     */
    String getKidFromUnsignedTokenHeader(String token);

    /**
     * Verify and decode the ID token payload
     */
    OIDCDecodePayload verifyAndDecodeToken(String token, String clientId);

    /**
     * Verify token signature using provided public key
     */
    boolean isTokenSignatureInvalid(String token, OIDCPublicKey publicKey);
}
package com.example.trace.admin.provider;

import com.example.trace.admin.models.OIDCDecodePayload;
import com.example.trace.admin.models.OIDCPublicKey;

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
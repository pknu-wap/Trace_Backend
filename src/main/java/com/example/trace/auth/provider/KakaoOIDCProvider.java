package com.example.trace.auth.provider;

import com.example.trace.auth.models.OIDCDecodePayload;
import com.example.trace.auth.models.OIDCPublicKey;
import com.example.trace.global.errorcode.AuthErrorCode;
import com.example.trace.global.exception.AuthException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOIDCProvider implements OIDCProvider {
    private static final String ISSUER = "https://kauth.kakao.com";

    private String decodeBase64(String encoded) {
        try {
            return new String(Base64.getUrlDecoder().decode(encoded));
        } catch (IllegalArgumentException e) {
            log.error("Invalid Base64 encoding in token header", e);
            throw new AuthException(AuthErrorCode.DECODE_ERROR);
        }
    }

    private JsonNode parseJson(String json) {
        try {
            return new ObjectMapper().readTree(json);
        } catch (IOException e) {
            log.error("Invalid JSON in token header", e);
            throw new AuthException(AuthErrorCode.PARSE_ERROR);
        }
    }

    private OIDCDecodePayload parsePayload(String payloadJson) {
        try {
            return new ObjectMapper().readValue(payloadJson, OIDCDecodePayload.class);
        } catch (IOException e) {
            log.error("Invalid JSON in token payload", e);
            throw new AuthException(AuthErrorCode.PARSE_ERROR);
        }
    }



    @Override
    public String getKidFromUnsignedTokenHeader(String token) {
        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            throw new AuthException(AuthErrorCode.INVALID_ID_TOKEN_FORMAT);
        }

        String headerJson = decodeBase64(parts[0]);
        JsonNode headerNode = parseJson(headerJson);

        if (!headerNode.has("kid")) {
            throw new AuthException(AuthErrorCode.KID_NOT_FOUND);
        }

        return headerNode.get("kid").asText();
    }

    @Override
    public OIDCDecodePayload verifyAndDecodeToken(String token, String clientId) {

        String[] parts = token.split("\\.");
        if (parts.length < 2) {
            throw new AuthException(AuthErrorCode.INVALID_ID_TOKEN_FORMAT);
        }

        String payloadJson = decodeBase64(parts[1]);
        OIDCDecodePayload payload = parsePayload(payloadJson);

        // Verify issuer
        if (!ISSUER.equals(payload.getIss())) {
            throw new AuthException(AuthErrorCode.INVALID_ID_TOKEN_ISSUER);
        }

        // Verify audience
        if (!clientId.equals(payload.getAud())) {
            throw new AuthException(AuthErrorCode.INVALID_ID_TOKEN_AUDIENCE);
        }

        // Verify expiration
        if (payload.getExp() < System.currentTimeMillis() / 1000) {
            throw new AuthException(AuthErrorCode.EXPIRED_ID_TOKEN);
        }

        return payload;

    }

    @Override
    public boolean isTokenSignatureInvalid(String token, OIDCPublicKey publicKey) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return true; // Invalid token format
            }

            // Prepare data to verify
            String dataToVerify = parts[0] + "." + parts[1];
            byte[] signature = Base64.getUrlDecoder().decode(parts[2]);

            // Convert public key components from base64
            byte[] modulusBytes = Base64.getUrlDecoder().decode(publicKey.getN());
            byte[] exponentBytes = Base64.getUrlDecoder().decode(publicKey.getE());

            // Create public key
            BigInteger modulus = new BigInteger(1, modulusBytes);
            BigInteger exponent = new BigInteger(1, exponentBytes);
            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey publicKeyObj = factory.generatePublic(spec);

            // Verify signature
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKeyObj);
            sig.update(dataToVerify.getBytes());

            return !sig.verify(signature); // Return true if signature is invalid
        } catch (Exception e) {
            log.error("Error verifying token signature", e);
            return true; // Consider any error as invalid signature
        }
    }
}
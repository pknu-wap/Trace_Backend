package com.example.trace.provider;

import com.example.trace.client.KakaoOAuthClient;
import com.example.trace.models.OIDCDecodePayload;
import com.example.trace.models.OIDCPublicKey;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    private final KakaoOAuthClient kakaoOAuthClient;

    @Override
    public String getKidFromUnsignedTokenHeader(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid token format");
            }

            String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
            JsonNode headerNode = new ObjectMapper().readTree(headerJson);

            if (!headerNode.has("kid")) {
                throw new IllegalArgumentException("No kid found in token header");
            }

            return headerNode.get("kid").asText();
        } catch (Exception e) {
            log.error("Error extracting kid from token header", e);
            throw new IllegalArgumentException("Failed to parse token header", e);
        }
    }

    @Override
    public OIDCDecodePayload verifyAndDecodeToken(String token, String clientId) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid token format");
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            OIDCDecodePayload payload = new ObjectMapper().readValue(payloadJson, OIDCDecodePayload.class);

            // Verify issuer
            if (!ISSUER.equals(payload.getIss())) {
                throw new IllegalArgumentException("Invalid token issuer");
            }

            // Verify audience
            if (!clientId.equals(payload.getAud())) {
                throw new IllegalArgumentException("Invalid token audience");
            }

            // Verify expiration
            if (payload.getExp() < System.currentTimeMillis() / 1000) {
                throw new IllegalArgumentException("Token has expired");
            }

            // We don't verify nonce here as it would have been provided by the client initially

            return payload;
        } catch (Exception e) {
            log.error("Error decoding token payload", e);
            throw new IllegalArgumentException("Failed to decode or verify token", e);
        }
    }

    @Override
    public boolean isTokenSignatureInvalid(String token, OIDCPublicKey publicKey) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
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

            return sig.verify(signature);
        } catch (Exception e) {
            log.error("Error verifying token signature", e);
            return false;
        }
    }
}
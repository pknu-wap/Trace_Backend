package com.example.trace.auth.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OIDCPublicKeyResponse {
    private List<OIDCPublicKey> keys;
}
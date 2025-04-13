package com.example.trace.admin.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OIDCPublicKey {
    private String kid;
    private String kty;
    private String alg;
    private String use;
    private String n;  // modulus
    private String e;  // exponent
}

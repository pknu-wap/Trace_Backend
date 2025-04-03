package com.example.jwttest.converter;


import com.example.jwttest.domain.User;
import lombok.Builder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Builder
public class AuthConverter {

    public static User toUser(Long kakaoid,String name, String password, PasswordEncoder passwordEncoder) {
        return User.builder()
                .kakaoid(kakaoid)
                .role("ROLE_USER")
                .password(passwordEncoder.encode(password))
                .name(name)
                .build();
    }
}

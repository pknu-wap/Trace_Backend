package com.example.jwttest.service;

import com.example.jwttest.PrincipalDetails;
import com.example.jwttest.Util.JwtUtil;
import com.example.jwttest.dto.JwtDto;
import com.example.jwttest.Util.RedisUtil;

import com.example.jwttest.exception.SecurityCustomException;
import com.example.jwttest.exception.TokenErrorCode;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final PrincipalDetailsService principalDetailsService;

    public boolean validateRefreshToken(String refreshToken) {
        // refreshToken validate
        String username = jwtUtil.getUsername(refreshToken);

        //redis 확인
        if (!redisUtil.hasKey(username)) {
            throw new SecurityCustomException(TokenErrorCode.INVALID_TOKEN);
        }
        return true;
    }
    public JwtDto reissueToken(String refreshToken) throws SignatureException {
        String username = jwtUtil.getUsername(refreshToken);

        UserDetails userDetails = principalDetailsService.loadUserByUsername(username);

        PrincipalDetails principalDetails = (PrincipalDetails) userDetails;

        return new JwtDto(
                jwtUtil.createJwtAccessToken(principalDetails),
                jwtUtil.createJwtRefreshToken(principalDetails)
        );
    }

}
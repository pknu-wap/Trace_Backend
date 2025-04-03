package com.example.jwttest.service;

import com.example.jwttest.converter.AuthConverter;
import com.example.jwttest.PrincipalDetails;
import com.example.jwttest.Util.JwtUtil;
import com.example.jwttest.Util.KakaoUtil;
import com.example.jwttest.converter.UserConverter;
import com.example.jwttest.domain.User;
import com.example.jwttest.dto.AuthResponseDto;
import com.example.jwttest.dto.JwtDto;
import com.example.jwttest.Util.RedisUtil;

import com.example.jwttest.dto.KakaoDto;
import com.example.jwttest.exception.SecurityCustomException;
import com.example.jwttest.exception.TokenErrorCode;
import com.example.jwttest.repository.UserRepository;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final PrincipalDetailsService principalDetailsService;
    private final KakaoUtil kakaoUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public AuthResponseDto oAuthLogin(String accessCode) {
            KakaoDto.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
            KakaoDto.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);
            Long kakaoid = kakaoProfile.getId();

            User user = userRepository.findByKakaoid(kakaoid)
                .orElseGet(() -> createNewUser(kakaoProfile));
            PrincipalDetails principalDetails = new PrincipalDetails(user);
            return new AuthResponseDto(
                UserConverter.toJoinResultDTO(user),
                jwtUtil.createJwtAccessToken(principalDetails),
                jwtUtil.createJwtRefreshToken(principalDetails)
            );
    }
    public boolean validateRefreshToken(String refreshToken) {
        // refreshToken validate
        String username = jwtUtil.getUsername(refreshToken);

        //redis 확인
        if (!redisUtil.hasKey(username)) {
            throw new SecurityCustomException(TokenErrorCode.INVALID_TOKEN);
        }
        return true;
    }

    private User createNewUser(KakaoDto.KakaoProfile kakaoProfile) {
        String tempPassword = UUID.randomUUID().toString();
        User newUser = AuthConverter.toUser(
                kakaoProfile.getId(),
                kakaoProfile.getKakaoAccount().getProfile().getNickname(),
                tempPassword,
                passwordEncoder
        );
        return userRepository.save(newUser);
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
package com.springboot.escape.domain.auth.service;

import com.springboot.escape.config.security.JwtTokenProvider;
import com.springboot.escape.domain.auth.dto.SignInRequestDto;
import com.springboot.escape.domain.auth.dto.SignInResponseDto;
import com.springboot.escape.domain.user.entity.User;
import com.springboot.escape.domain.user.repository.UserRepository;
import com.springboot.escape.domain.auth.exception.AuthErrorCode;
import com.springboot.escape.domain.user.exception.UserErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {
    private final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    public AuthServiceImpl(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder, RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public SignInResponseDto signIn(SignInRequestDto signInRequestDto) {
        LOGGER.info("[signIn] signDataHandler로 회원 정보 요청");
        User user = userRepository.findByEmail(signInRequestDto.getEmail()).orElseThrow(UserErrorCode.SIGN_IN_USER_NOT_FOUND::defaultException);
        LOGGER.info("[signIn] Id : {}", signInRequestDto.getEmail());
        if (!passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())) {
            throw AuthErrorCode.INVALID_PASSWORD.defaultException();
        }
        LOGGER.info("[signIn] 패스워드 일치");
        LOGGER.info("[signIn] SignInResponseDto 객체 생성");

        return SignInResponseDto.builder()
                .access_token(jwtTokenProvider.createAccessToken(String.valueOf(user.getEmail()),user.getRole()))
                .refresh_token(jwtTokenProvider.createRefreshToken(String.valueOf(user.getEmail())))
                .build();
    }

    @Override
    public SignInResponseDto reissue(String refreshToken, String accessToken) {
        String userEmail = jwtTokenProvider.getUserEmailFromRefreshToken(refreshToken);
        String savedRefreshToken = redisTemplate.opsForValue().get("refresh:"+userEmail);
        // 1.저장된 refreshToken과 일치하는지 검사
        if (!refreshToken.equals(savedRefreshToken)) {
            throw AuthErrorCode.INVALID_REFRESH_TOKEN.defaultException();
        }
        this.addBlackList(accessToken);

        User user = userRepository.findByEmail(userEmail).orElseThrow(AuthErrorCode.INVALID_REFRESH_TOKEN::defaultException);
        String createdAccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
        String createdRefreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        return SignInResponseDto.builder()
                .access_token(createdAccessToken)
                .refresh_token(createdRefreshToken)
                .build();
    }
    private void addBlackList(String accessToken) {
        // DESC : accessToken이 유효할 경우 redis blacklist에 accessToken 추가
        if (jwtTokenProvider.isValidToken(accessToken)) {
            this.redisTemplate.opsForValue().set(
                    "blacklist:"+accessToken,
                    "logout",
                    this.jwtTokenProvider.getExpiration(accessToken),
                    TimeUnit.MILLISECONDS
            );
        }
    }
    @Override
    public void signOut(String refreshToken, String accessToken) {
        // 1. accessToken 검증
        if (!jwtTokenProvider.isValidToken(accessToken)) {
            throw AuthErrorCode.INVALID_ACCESS_TOKEN.defaultException();
        }
        // 2. refresh Token 검증
        // 2-1. accessToken으로 사용자 정보 가져옴
        String userEmail = jwtTokenProvider.getUserEmailFromAccessToken(accessToken);
        // 2-2. 해당 사용자 정보로 refershToken 가져옴
        String selectedRefreshToken = this.redisTemplate.opsForValue().get("refresh:" + userEmail);
        // 2-3. 받은refresh Token과 조회한 refreshToken이 같은지 확인
        if (!refreshToken.equals(selectedRefreshToken)) {
            throw AuthErrorCode.INVALID_REFRESH_TOKEN.defaultException();
        }
        // 3. redis에 refresh Token 삭제
        this.redisTemplate.delete("refresh:" + userEmail);
        // 4. redis blacklist에 accessToken 추가
        this.redisTemplate.opsForValue().set(
                "blacklist:"+accessToken,
                "logout",
                this.jwtTokenProvider.getExpiration(accessToken),
                TimeUnit.MILLISECONDS
        );
    }
}

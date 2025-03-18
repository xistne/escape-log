package com.springboot.escape.service.impl;

import com.springboot.escape.config.security.JwtTokenProvider;
import com.springboot.escape.data.dto.SignInRequestDto;
import com.springboot.escape.data.dto.SignInResponseDto;
import com.springboot.escape.data.dto.SignUpRequestDto;
import com.springboot.escape.data.dto.SignUpResponseDto;
import com.springboot.escape.data.entity.User;
import com.springboot.escape.data.repository.UserRepository;
import com.springboot.escape.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

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
    public SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto) {
        LOGGER.info("[signUp] 회원 가입 정보 전달");
        User user;
        if (signUpRequestDto.getRole().equalsIgnoreCase("admin")) {
            user = User.builder()
                    .email(signUpRequestDto.getEmail())
                    .name(signUpRequestDto.getName())
                    .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                    .roles(Collections.singletonList("ROLE_ADMIN"))
                    .build();
        } else {
            user = User.builder()
                    .email(signUpRequestDto.getEmail())
                    .name(signUpRequestDto.getName())
                    .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                    .roles(Collections.singletonList("ROLE_USER"))
                    .build();
        }
        User savedUser = userRepository.save(user);
        SignUpResponseDto signUpResponseDto = new SignUpResponseDto();

        LOGGER.info("[signUp] userEntity 값이 들어왔는지 확인 후 결과값 주입");
        if (!savedUser.getName().isEmpty()) {
            LOGGER.info("[signUp] 정상 처리 완료");
            setSuccessResult(signUpResponseDto);
        } else {
            LOGGER.info("[signUp] 실패 처리 완료");
            setFailResult(signUpResponseDto);
        }
        return signUpResponseDto;
    }

    @Override
    public SignInResponseDto signIn(SignInRequestDto signInRequestDto) {
        LOGGER.info("[signIn] signDataHandler로 회원 정보 요청");
        User user = userRepository.getByEmail(signInRequestDto.getEmail());
        LOGGER.info("[signIn] Id : {}", signInRequestDto.getEmail());
        if (!passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException();
        }
        LOGGER.info("[signIn] 패스워드 일치");
        LOGGER.info("[signIn] SignInResponseDto 객체 생성");
        SignInResponseDto signInResponseDto = SignInResponseDto.builder()
                .access_token(jwtTokenProvider.createAccessToken(String.valueOf(user.getEmail()),user.getRoles()))
                .refresh_token(jwtTokenProvider.createRefreshToken(String.valueOf(user.getEmail())))
                .build();
        LOGGER.info("[signIn] SignInResponseDto 객체에 값 주입");
        setSuccessResult(signInResponseDto);

        return signInResponseDto;
    }

    @Override
    public SignInResponseDto reissue(String refreshToken) {
        String userEmail = jwtTokenProvider.getUserEmailFromToken(refreshToken);
        String savedRefreshToken = redisTemplate.opsForValue().get(userEmail);

        if (!refreshToken.equals(savedRefreshToken)) {
            throw new RuntimeException();
        }

        User user = userRepository.getByEmail(userEmail);
        String createdAccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRoles());
        String createdRefreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());


        SignInResponseDto signInResponseDto = SignInResponseDto.builder()
                .access_token(createdAccessToken)
                .refresh_token(createdRefreshToken)
                .build();

        setSuccessResult(signInResponseDto);

        return signInResponseDto;
    }

    private void setSuccessResult(SignUpResponseDto result) {
        result.setSuccess(true);
        result.setMsg("Success");
    }

    private void setFailResult(SignUpResponseDto result) {
        result.setSuccess(false);
        result.setMsg("Fail");
    }
}

package com.springboot.escape.domain.auth.service;

import com.springboot.escape.domain.auth.dto.SignInRequestDto;
import com.springboot.escape.domain.auth.dto.SignInResponseDto;

public interface AuthService {
    SignInResponseDto signIn(SignInRequestDto signInRequestDto);
    SignInResponseDto reissue(String refreshToken, String accessToken);
    void signOut(String refreshToken, String accessToken);
}

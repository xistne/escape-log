package com.springboot.escape.service;

import com.springboot.escape.data.dto.SignInRequestDto;
import com.springboot.escape.data.dto.SignInResponseDto;

public interface AuthService {
    SignInResponseDto signIn(SignInRequestDto signInRequestDto);
    SignInResponseDto reissue(String refreshToken, String accessToken);
    void signOut(String refreshToken, String accessToken);
}

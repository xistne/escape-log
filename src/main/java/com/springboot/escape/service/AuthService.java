package com.springboot.escape.service;

import com.springboot.escape.data.dto.SignInRequestDto;
import com.springboot.escape.data.dto.SignInResponseDto;
import com.springboot.escape.data.dto.SignUpRequestDto;
import com.springboot.escape.data.dto.SignUpResponseDto;

public interface AuthService {
    SignUpResponseDto signUp(SignUpRequestDto signUpRequestDto);
    SignInResponseDto signIn(SignInRequestDto signInRequestDto);
    SignInResponseDto reissue(String refreshToken);
    String signOut(String refreshToken, String accessToken);
}

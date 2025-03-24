package com.springboot.escape.service;

import com.springboot.escape.data.dto.SignInRequestDto;
import com.springboot.escape.data.dto.SignInResponseDto;
import com.springboot.escape.data.dto.SignUpRequestDto;
import com.springboot.escape.data.dto.SignUpResponseDto;

public interface AuthService {
    void signUp(SignUpRequestDto signUpRequestDto);
    SignInResponseDto signIn(SignInRequestDto signInRequestDto);
    SignInResponseDto reissue(String refreshToken, String accessToken);
    void signOut(String refreshToken, String accessToken);
}

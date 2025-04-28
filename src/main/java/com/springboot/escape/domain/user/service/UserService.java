package com.springboot.escape.domain.user.service;

import com.springboot.escape.domain.user.dto.SignUpRequestDto;

public interface UserService {
    void signUp(SignUpRequestDto signUpRequestDto);
}

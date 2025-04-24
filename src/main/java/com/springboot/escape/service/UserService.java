package com.springboot.escape.service;

import com.springboot.escape.data.dto.SignUpRequestDto;

public interface UserService {
    void signUp(SignUpRequestDto signUpRequestDto);
}

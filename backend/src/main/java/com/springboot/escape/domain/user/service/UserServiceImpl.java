package com.springboot.escape.domain.user.service;

import com.springboot.escape.domain.user.dto.SignUpRequestDto;
import com.springboot.escape.domain.user.constant.RoleEnum;
import com.springboot.escape.domain.user.entity.User;
import com.springboot.escape.domain.user.repository.UserRepository;
import com.springboot.escape.domain.user.exception.UserErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void signUp(SignUpRequestDto signUpRequestDto) {
        LOGGER.info("[signUp] 회원 가입 정보 전달");
        RoleEnum userRoleEnum = signUpRequestDto.getRole().equalsIgnoreCase("admin")
                ? RoleEnum.ADMIN
                : RoleEnum.USER;

        userRepository.findByEmail(signUpRequestDto.getEmail())
                .ifPresent(user -> {
                    throw UserErrorCode.EMAIL_ALREADY_EXISTS.defaultException();
                });

        User user = User.builder()
                .email(signUpRequestDto.getEmail())
                .name(signUpRequestDto.getName())
                .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                .role(userRoleEnum)
                .build();
        User savedUser = userRepository.save(user);
        if (!savedUser.getName().isEmpty()) {
            LOGGER.info("[signUp] 정상 처리 완료");
        } else {
            LOGGER.info("[signUp] 실패 처리 완료");
            throw UserErrorCode.SIGN_UP_FAILED.defaultException();
        }
    }
}

package com.springboot.escape.domain.user.service;

import com.springboot.escape.domain.user.constant.RoleEnum;
import com.springboot.escape.domain.user.dto.SignUpRequestDto;
import com.springboot.escape.domain.user.entity.User;
import com.springboot.escape.domain.user.exception.UserErrorCode;
import com.springboot.escape.domain.user.exception.UserException;
import com.springboot.escape.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserRepository userRepository;

    @Nested
    class SignUpTest {
        @Test
        @DisplayName("회원가입 성공")
        void shouldSignUpSuccessfully() {
            // given
            SignUpRequestDto signUpRequestDto = new SignUpRequestDto(
                    "test@email.com",
                    "1234ABcd!",
                    "test",
                    "user");
            User user = User.builder()
                    .email("test@email.com")
                    .name("test")
                    .password("encoded-password")
                    .role(RoleEnum.USER)
                    .build();
            given(userRepository.findByEmail(signUpRequestDto.getEmail())).willReturn(Optional.empty());
            given(passwordEncoder.encode(signUpRequestDto.getPassword())).willReturn("encoded-password");
            given(userRepository.save(any(User.class))).willReturn(user); // DESC : ArgumentMatchers

            // when
            userService.signUp(signUpRequestDto);

            // then
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("회원가입 실패 - 이미 가입된 이메일")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            // given
            SignUpRequestDto signUpRequestDto = new SignUpRequestDto(
                    "test@email.com",
                    "1234ABcd!",
                    "test",
                    "user");
            given(userRepository.findByEmail(signUpRequestDto.getEmail())).willReturn(Optional.of(mock(User.class)));

            // when & then
            assertThatThrownBy(() -> userService.signUp(signUpRequestDto))
                    .isInstanceOf(UserException.class)
                    .hasMessageContaining(UserErrorCode.EMAIL_ALREADY_EXISTS.defaultMessage());
        }
    }
}

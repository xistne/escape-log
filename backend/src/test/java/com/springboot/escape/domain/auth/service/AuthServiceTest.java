package com.springboot.escape.domain.auth.service;

import com.springboot.escape.configuration.security.JwtTokenProvider;
import com.springboot.escape.domain.auth.dto.SignInRequestDto;
import com.springboot.escape.domain.auth.dto.SignInResponseDto;
import com.springboot.escape.domain.auth.exception.AuthErrorCode;
import com.springboot.escape.domain.auth.exception.AuthException;
import com.springboot.escape.domain.user.constant.RoleEnum;
import com.springboot.escape.domain.user.entity.User;
import com.springboot.escape.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class
AuthServiceTest {

    @InjectMocks
    AuthServiceImpl authService;

    @Mock
    UserRepository userRepository;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Nested
    class SignInTest {
        @Test
        @DisplayName("로그인 성공")
        void shouldReturnTokenWhenCredentialsAreValid() {
            // given
            SignInRequestDto signInRequestDto = new SignInRequestDto("test@email.com","1234ABcd!");
            User user = User.builder()
                    .email("test@email.com")
                    .name("test")
                    .password("1234ABcd!")
                    .role(RoleEnum.USER)
                    .build();
            given(userRepository.findByEmail(signInRequestDto.getEmail())).willReturn(Optional.of(user));
            given(passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())).willReturn(true);
            given(jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole())).willReturn("access-token");
            given(jwtTokenProvider.createRefreshToken(user.getEmail())).willReturn("refresh-token");

            // when
            SignInResponseDto result = authService.signIn(signInRequestDto);

            // then
            assertThat(result.getAccess_token()).isEqualTo("access-token");
            assertThat(result.getRefresh_token()).isEqualTo("refresh-token");
        }
        @Test
        @DisplayName("로그인 실패 - 비밀번호 불일치")
        void shouldThrowExceptionWhenPasswordDoesNotMatch() {
            // given
            SignInRequestDto signInRequestDto = new SignInRequestDto("test@email.com","1234ABcd!");
            User user = User.builder()
                    .email("test@email.com")
                    .name("test")
                    .password("1234ABcd!")
                    .role(RoleEnum.USER)
                    .build();
            given(userRepository.findByEmail(signInRequestDto.getEmail())).willReturn(Optional.of(user));
            given(passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.signIn(signInRequestDto))
                    .isInstanceOf(AuthException.class)
                    .hasMessageContaining(AuthErrorCode.INVALID_PASSWORD.defaultMessage());

        }
    }

    @Nested
    class ReissueTest {
        @Test
        @DisplayName("토큰 재발행 성공")
        void shouldReturnTokenWhenRefreshTokenIsValid() {
            // given
            String refreshToken = "refresh-token";
            String accessToken = "access-token";
            User user = User.builder()
                    .email("test@email.com")
                    .name("test")
                    .password("1234ABcd!")
                    .role(RoleEnum.USER)
                    .build();
            given(jwtTokenProvider.getUserEmailFromRefreshToken(refreshToken)).willReturn("test@email.com");
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.get("refresh:" + "test@email.com")).willReturn("refresh-token");
            given(jwtTokenProvider.isValidToken(accessToken)).willReturn(true);
            given(userRepository.findByEmail("test@email.com")).willReturn(Optional.of(user));
            given(jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole())).willReturn("access-token");
            given(jwtTokenProvider.createRefreshToken(user.getEmail())).willReturn("refresh-token");

            // when
            SignInResponseDto result = authService.reissue(refreshToken, accessToken);

            // then
            assertThat(result.getAccess_token()).isEqualTo("access-token");
            assertThat(result.getRefresh_token()).isEqualTo("refresh-token");
        }
        @Test
        @DisplayName("토큰 재발행 실패 - 리프레시 토큰 불일치")
        void shouldThrowExceptionWhenRefreshTokenIsInvalid() {
            // given
            String refreshToken = "refresh-token";
            String accessToken = "access-token";
            given(jwtTokenProvider.getUserEmailFromRefreshToken(refreshToken)).willReturn("test@email.com");
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.get("refresh:" + "test@email.com")).willReturn("refresh-token");
            given(jwtTokenProvider.isValidToken(accessToken)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.reissue(refreshToken, accessToken))
                    .isInstanceOf(AuthException.class)
                    .hasMessageContaining(AuthErrorCode.INVALID_REFRESH_TOKEN.defaultMessage());
        }
    }

    @Nested
    class SignOutTest {
        @Test
        @DisplayName("로그아웃 성공")
        void shouldLogoutSuccessfullyWhenTokenIsValid() {
            // given
            String refreshToken = "refresh-token";
            String accessToken = "access-token";
            String userEmail = "test@email.com";
            given(jwtTokenProvider.isValidToken(accessToken)).willReturn(true);
            given(jwtTokenProvider.getUserEmailFromAccessToken(accessToken)).willReturn("test@email.com");
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.get("refresh:" + "test@email.com")).willReturn("refresh-token");
            given(jwtTokenProvider.getExpiration(accessToken)).willReturn(100000L);

            // when
            authService.signOut(refreshToken, accessToken);

            // then
            verify(redisTemplate).delete("refresh:" + userEmail);
            verify(redisTemplate.opsForValue()).set(
                    "blacklist:" + accessToken,
                    "logout",
                    100000L,
                    TimeUnit.MILLISECONDS
            );
        }

        @Test
        @DisplayName("로그아웃 실패 - 유효하지 않은 AccessToken")
        void shouldThrowExceptionWhenAccessTokenIsInvalid() {
            // given
            String refreshToken = "refresh-token";
            String accessToken = "access-token";
            given(jwtTokenProvider.isValidToken(accessToken)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.signOut(refreshToken, accessToken))
                    .isInstanceOf(AuthException.class)
                    .hasMessageContaining(AuthErrorCode.INVALID_ACCESS_TOKEN.defaultMessage());
        }

        @Test
        @DisplayName("로그아웃 실패 - 유효하지 않은 RefreshToekn")
        void shouldThrowExceptionWhenRefreshTokenIsInvalid() {
            // given
            String refreshToken = "refresh-token";
            String accessToken = "access-token";
            given(jwtTokenProvider.isValidToken(accessToken)).willReturn(true);
            given(jwtTokenProvider.getUserEmailFromAccessToken(accessToken)).willReturn("test@email.com");
            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(valueOperations.get("refresh:" + "test@email.com")).willReturn("");

            // when & then
            assertThatThrownBy(() -> authService.signOut(refreshToken, accessToken))
                    .isInstanceOf(AuthException.class)
                    .hasMessageContaining(AuthErrorCode.INVALID_REFRESH_TOKEN.defaultMessage());
        }

    }
}

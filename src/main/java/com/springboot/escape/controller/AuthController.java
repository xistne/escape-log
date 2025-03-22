package com.springboot.escape.controller;

import com.springboot.escape.data.dto.SignInRequestDto;
import com.springboot.escape.data.dto.SignInResponseDto;
import com.springboot.escape.data.dto.SignUpRequestDto;
import com.springboot.escape.data.dto.SignUpResponseDto;
import com.springboot.escape.exception.AuthErrorCode;
import com.springboot.escape.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/sign-in")
    public ResponseEntity<SignInResponseDto> signIn(@RequestBody SignInRequestDto signInRequestDto) {
        return ResponseEntity.ok(authService.signIn(signInRequestDto));
    }

    @PostMapping(value = "/sign-up")
    public ResponseEntity<SignUpResponseDto> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        return ResponseEntity.ok(authService.signUp(signUpRequestDto));
    }

    @PostMapping("/reissue")
    public ResponseEntity<SignInResponseDto> reissue(@RequestHeader(value="REFRESH-TOKEN") String refreshToken) {
        return ResponseEntity.ok(authService.reissue(refreshToken));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<String> signOut(@RequestHeader(value="REFRESH-TOKEN") String refreshToken,
                                          @RequestHeader(value="X-AUTH-TOKEN") String accessToken) {
        return ResponseEntity.ok(authService.signOut(refreshToken, accessToken));
    }

    @GetMapping(value = "/exception")
    public void exception() {
        throw AuthErrorCode.FORBIDDEN_ACCESS.defaultException();
    }
}

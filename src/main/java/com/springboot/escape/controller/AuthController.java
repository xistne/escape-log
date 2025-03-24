package com.springboot.escape.controller;

import com.springboot.escape.data.dto.*;
import com.springboot.escape.exception.AuthErrorCode;
import com.springboot.escape.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<SignInResponseDto>> signIn(@RequestBody SignInRequestDto signInRequestDto) {
        return ResponseEntity.ok(ApiResponse.success(authService.signIn(signInRequestDto), HttpStatus.OK));
    }

    @PostMapping(value = "/sign-up")
    public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody SignUpRequestDto signUpRequestDto) {
        authService.signUp(signUpRequestDto);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<SignInResponseDto>> reissue(@RequestHeader(value="REFRESH-TOKEN") String refreshToken) {
        return ResponseEntity.ok(ApiResponse.success(authService.reissue(refreshToken), HttpStatus.OK));
    }

    @PostMapping("/sign-out")
    public ResponseEntity<ApiResponse<Void>> signOut(@RequestHeader(value="REFRESH-TOKEN") String refreshToken,
                                                       @RequestHeader(value="X-AUTH-TOKEN") String accessToken) {
        authService.signOut(refreshToken, accessToken);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK));
    }

    @GetMapping(value = "/exception")
    public void exception() {
        throw AuthErrorCode.FORBIDDEN_ACCESS.defaultException();
    }
}

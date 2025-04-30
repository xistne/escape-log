package com.springboot.escape.domain.auth.controller;

import com.springboot.escape.domain.auth.dto.SignInRequestDto;
import com.springboot.escape.domain.auth.dto.SignInResponseDto;
import com.springboot.escape.domain.auth.exception.AuthErrorCode;
import com.springboot.escape.global.constant.ApiPrefix;
import com.springboot.escape.response.ApiResponse;
import com.springboot.escape.domain.auth.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiPrefix.API + "/auth")
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

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<SignInResponseDto>> reissue(@RequestHeader(value="REFRESH-TOKEN") String refreshToken,
                                                                  @RequestHeader(value="X-AUTH-TOKEN") String accessToken) {
        return ResponseEntity.ok(ApiResponse.success(authService.reissue(refreshToken, accessToken), HttpStatus.OK));
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

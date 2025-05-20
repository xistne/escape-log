package com.springboot.escape.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Schema(description = "로그인 성공 시 반환되는 토큰 정보 객체")
public class SignInResponseDto {
    @Schema(description = "Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE2OTIwMDAwMDAsImV4cCI6MTY5MjAwMzYwMH0.4jYZDEWKoDe2Zd82MfrrvHUWTvA0vD4KvIxC9jc9OvU")
    private String access_token;
    @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZWZyZXNoX3VzZXJAZXhhbXBsZS5jb20iLCJ0eXBlIjoicmVmcmVzaCIsImlhdCI6MTY5MjAwMDAwMCwiZXhwIjoxNjkzMjAwMDAwfQ.W-rxBziM9sLWhzM9AAXTDa04eWSWnctXKc-nD_f1uAQ")
    private String refresh_token;
}

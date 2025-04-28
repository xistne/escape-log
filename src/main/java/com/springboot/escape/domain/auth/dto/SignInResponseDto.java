package com.springboot.escape.domain.auth.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SignInResponseDto {
    private String access_token;
    private String refresh_token;
}

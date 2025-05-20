package com.springboot.escape.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "로그인 요청에 사용되는 데이터 객체")
public class SignInRequestDto {
    @Schema(description = "이메일", example = "test@email.com")
    private String email;

    @Schema(description = "비밀번호", example = "1234ABcd!")
    private String password;
}

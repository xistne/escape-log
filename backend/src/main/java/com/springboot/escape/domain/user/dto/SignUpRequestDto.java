package com.springboot.escape.domain.user.dto;

import com.springboot.escape.domain.user.annotation.ValidRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "회원가입 요청에 사용되는 데이터 객체")
public class SignUpRequestDto {
    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message="이메일 형식이 잘못되었습니다.")
    @Schema(description = "이메일 (기본 이메일 형식)", example = "test@email.com")
    private String email;
    @NotBlank(message = "비밀번호를 입력하세요.")
    @Pattern(regexp = "^(?!.*[ㄱ-ㅎㅏ-ㅣ가-힣])(?=.*[A-Z])(?!.*\\s)(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*()-+=]).{8,16}$",
            message = "비밀번호는 8~16자, 영문 대/소문자, 숫자, 특수문자(!@#$%^&*()-+=)를 포함해야 하며, 공백과 한글은 허용되지 않습니다.")
    @Schema(description = "비밀번호 (8~16자, 영문 대/소문자, 숫자, 특수문자(!@#$%^&*()-+=)를 포함해야 하며, 공백과 한글은 허용되지 않음)", example = "1234ABcd!")
    private String password;
    @NotBlank(message = "이름을 입력하세요.")
    @Schema(description = "이름", example = "홍길동")
    private String name;
    @NotBlank(message = "권한을 입력하세요.")
    @ValidRole
    @Schema(description = "권한 (user, admin)", example = "user")
    private String role;
}

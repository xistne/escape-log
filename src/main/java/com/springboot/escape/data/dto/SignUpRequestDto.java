package com.springboot.escape.data.dto;

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
public class SignUpRequestDto {
    @NotBlank(message = "이메일을 입력하세요.")
    @Email(message="이메일 형식이 잘못되었습니다.")
    private String email;
    @NotBlank(message = "비밀번호를 입력하세요.")
    @Pattern(regexp = "^(?!.*[ㄱ-ㅎㅏ-ㅣ가-힣])(?=.*[A-Z])(?!.*\\s)(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*()-+=]).{8,16}$",
            message = "비밀번호는 8~16자, 영문 대/소문자, 숫자, 특수문자(!@#$%^&*()-+=)를 포함해야 하며, 공백과 한글은 허용되지 않습니다.")
    private String password;
    @NotBlank(message = "이름을 입력하세요.")
    private String name;
    @NotBlank
    private String role;
}

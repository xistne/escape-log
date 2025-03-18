package com.springboot.escape.data.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SignInRequestDto {
    private String email;
    private String password;
}

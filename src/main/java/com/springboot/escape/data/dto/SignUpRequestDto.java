package com.springboot.escape.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SignUpRequestDto {
    private String email;
    private String password;
    private String name;
    private String role;
}

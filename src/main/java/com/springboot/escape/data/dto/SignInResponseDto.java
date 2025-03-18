package com.springboot.escape.data.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class SignInResponseDto extends SignUpResponseDto {
    private String access_token;
    private String refresh_token;

    @Builder
    public SignInResponseDto(boolean success, String msg, String access_token, String refresh_token) {
        super(success, msg);
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }
}

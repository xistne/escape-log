package com.springboot.escape.data.entity;

import com.springboot.escape.exception.AuthErrorCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum RoleEnum {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    private final String authority;

    RoleEnum(String authority) {
        this.authority = authority;
    }

    public static RoleEnum fromAuthority(String authority) {
        return Arrays.stream(values())
                .filter(e -> e.authority.equals(authority))
                .findFirst()
                .orElseThrow(AuthErrorCode.INVALID_ROLE::defaultException);
    }
}

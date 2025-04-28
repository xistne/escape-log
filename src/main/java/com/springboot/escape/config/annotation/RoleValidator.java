package com.springboot.escape.config.annotation;

import com.springboot.escape.data.entity.RoleEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class RoleValidator implements ConstraintValidator<ValidRole, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return false;
        }
        return Arrays.stream(RoleEnum.values())
                .anyMatch(roleEnum -> roleEnum.name().equalsIgnoreCase(value));
    }
}

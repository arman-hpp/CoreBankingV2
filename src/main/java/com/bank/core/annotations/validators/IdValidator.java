package com.bank.core.annotations.validators;

import com.bank.core.annotations.ValidId;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IdValidator implements ConstraintValidator<ValidId, Long> {
    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        return value != null && value > 0;
    }
}
package com.bank.core.annotations;

import com.bank.core.annotations.validators.IdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = IdValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidId {
    String message() default "ID must be a positive number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
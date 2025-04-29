package com.bank.exceptions;

import lombok.Getter;
import org.jetbrains.annotations.PropertyKey;

@Getter
public class BusinessException extends RuntimeException {
    private final String resourceKey;
    private final Object[] args;

    public BusinessException(@PropertyKey(resourceBundle = "messages.messages") String resourceKey, Object... args) {
        super(resourceKey);
        this.resourceKey = resourceKey;
        this.args = args;
    }
}

package com.bank.core.exceptions;

import lombok.Getter;
import org.jetbrains.annotations.PropertyKey;

import java.util.Objects;

/**
 * Custom exception representing a business rule violation,
 * with support for localized messages using resource bundles.
 */
@Getter
public class BusinessException extends RuntimeException {
    private final String resourceKey;
    private final Object[] args;

    /**
     * Constructs a new BusinessException with a message key and optional arguments.
     *
     * @param resourceKey the resource bundle key for localization.
     * @param args optional arguments to replace placeholders in the message.
     */
    public BusinessException(
            @PropertyKey(resourceBundle = "messages.messages") String resourceKey,
            Object... args) {
        super(resourceKey);
        this.resourceKey = Objects.requireNonNull(resourceKey, "Resource key must not be null");
        this.args = args != null ? args : new Object[0];
    }
}


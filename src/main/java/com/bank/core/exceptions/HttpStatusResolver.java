package com.bank.core.exceptions;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class HttpStatusResolver {
    private static final Map<String, HttpStatus> RESOURCE_STATUS_MAP = Map.of(
            ".notFound", HttpStatus.NOT_FOUND,
            ".invalidData", HttpStatus.BAD_REQUEST,
            ".noAccess", HttpStatus.FORBIDDEN,
            ".dup", HttpStatus.CONFLICT
    );

    public static HttpStatus resolve(String resourceKey) {
        return RESOURCE_STATUS_MAP.entrySet().stream()
                .filter(entry -> resourceKey.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}

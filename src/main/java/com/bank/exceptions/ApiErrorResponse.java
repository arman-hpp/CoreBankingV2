package com.bank.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ApiErrorResponse {
    private final String error;
    private final String message;
    private final int status;
    private final LocalDateTime timestamp;

    public ApiErrorResponse(String error, String message, int status, LocalDateTime timestamp) {
        this.error = error;
        this.message = message;
        this.status = status;
        this.timestamp = timestamp;
    }
}

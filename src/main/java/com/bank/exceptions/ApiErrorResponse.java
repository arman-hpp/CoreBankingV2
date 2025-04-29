package com.bank.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public record ApiErrorResponse(String error, String message, int status, LocalDateTime timestamp) {
}

package com.bank.core.exceptions;


import java.time.LocalDateTime;

public record ApiErrorResponse(String error, String message, int status, LocalDateTime timestamp) {
}

package com.bank.exceptions;


import java.time.LocalDateTime;

public record ApiErrorResponse(String error, String message, int status, LocalDateTime timestamp) {
}

package com.bank.core.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException ex, Locale locale) {
        logger.warn("Business exception: {}", ex.getMessage(), ex);
        var message = messageSource.getMessage(ex.getResourceKey(), ex.getArgs(), locale);
        var httpStatus = HttpStatusResolver.resolve(ex.getResourceKey());
        return buildResponse("Business Error", message, httpStatus);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex, Locale locale) {
        logger.warn("Validation exception: {}", ex.getMessage(), ex);
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());
        var joinedErrors = String.join(", ", errors);
        var message = messageSource.getMessage("error.public.http.argument.validation", new Object[]{joinedErrors}, locale);
        return buildResponse("Validation Error", message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex, Locale locale) {
        logger.error("Access denied exception caught", ex);
        var message = messageSource.getMessage("error.public.http.access.denied", null, locale);
        return buildResponse("Forbidden", message, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception ex, Locale locale) {
        logger.error("Unhandled exception caught", ex);
        var message = messageSource.getMessage("error.public.http.unexpected", null, locale);
        return buildResponse("Internal Server Error", message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiErrorResponse> buildResponse(String error, String message, HttpStatus status) {
        var response = new ApiErrorResponse(
                error,
                message,
                status.value(),
                LocalDateTime.now().toString()
        );
        return ResponseEntity.status(status).body(response);
    }
}

package com.bank.core.exceptions;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiErrorResponse", description = "Structure for API error responses")
public record ApiErrorResponse(@Schema(description = "Short error identifier", example = "BAD_REQUEST") String error,
                               @Schema(description = "Detailed error message", example = "Invalid input data") String message,
                               @Schema(description = "HTTP status code", example = "400") int status,
                               @Schema(description = "Time of the error", example = "2025-05-13T10:23:00") String timestamp) {
}
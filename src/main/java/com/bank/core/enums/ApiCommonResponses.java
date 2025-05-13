package com.bank.core.enums;

import lombok.Getter;

/**
 * Defines HTTP status codes and their descriptions as constants.
 */
@Getter
public enum ApiCommonResponses {
    BAD_REQUEST(400,"Validation Error", "Invalid request due to incorrect parameters or malformed data"),
    UNAUTHORIZED(401,"Unauthorized", "Authentication failed or credentials are missing"),
    FORBIDDEN(403, "Forbidden","Access denied due to insufficient permissions"),
    NOT_FOUND(404, "Not Found", "Requested resource not found"),
    CONFLICT(409, "Conflict", "Request conflicts with the current state of the resource"),
    UNPROCESSABLE_ENTITY(422,"Business Error", "Unprocessable request due to business rule violation"),
    TOO_MANY_REQUESTS(429, "Too Many Requests", "Too many requests, rate limit exceeded"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error", "Unexpected server error, please try again later");

    private final Integer code;
    private final String description;
    private final String error;

    ApiCommonResponses(Integer code, String error, String description) {
        this.code = code;
        this.error = error;
        this.description = description;
    }
}
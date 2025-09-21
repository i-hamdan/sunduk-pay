package com.bxb.sunduk_pay.exception;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Standard error response structure for API exceptions.
 * <p>
 * Contains the timestamp, HTTP status code, error type, detailed message,
 * and the API path where the error occurred.
 * </p>
 */
@Data
public class ErrorResponse {
    private LocalDateTime localDateTime;
    private Integer status;
    private String error;
    private String message;
    private String path;

    /**
     * Constructs a new {@code ErrorResponse} with all fields.
     *
     * @param localDateTime timestamp when the error occurred
     * @param status HTTP status code of the error
     * @param error short description of the error type
     * @param message detailed message explaining the error
     * @param path the API path where the error occurred
     */
    public ErrorResponse(LocalDateTime localDateTime,
                         Integer status,String error,
                         String message, String path) {
        this.error = error;
        this.localDateTime = localDateTime;
        this.message = message;
        this.path = path;
        this.status = status;
    }
}

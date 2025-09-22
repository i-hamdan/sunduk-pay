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

    /** Timestamp when the error occurred */
    private LocalDateTime localDateTime;

    /** HTTP status code of the error */
    private Integer status;

    /** Short description of the error type */
    private String error;

    /** Detailed message explaining the error */
    private String message;

    /** The API path where the error occurred */
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
    public ErrorResponse(final LocalDateTime localDateTime,
                         final Integer status, final String error,
                         final String message, final String path) {
        this.error = error;
        this.localDateTime = localDateTime;
        this.message = message;
        this.path = path;
        this.status = status;
    }
}

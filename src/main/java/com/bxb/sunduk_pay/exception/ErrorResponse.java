package com.bxb.sunduk_pay.exception;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ErrorResponse {

    /** Timestamp when the error occurred. */
    private LocalDateTime localDateTime;

    /** HTTP status code of the error. */
    private Integer status;

    /** Short description of the error type. */
    private String error;

    /** Detailed message explaining the error. */
    private String message;

    /** The API path where the error occurred. */
    private String path;

}

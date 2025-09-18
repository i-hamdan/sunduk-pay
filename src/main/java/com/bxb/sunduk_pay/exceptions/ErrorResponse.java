package com.bxb.sunduk_pay.exceptions;

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

    /** Timestamp when the error occurred. */
    private LocalDateTime localDateTime;

    /** HTTP status code of the error (e.g., 400, 500). */
    private Integer status;

    /** Short description of the error type. */
    private String error;

    /** Detailed error message for debugging or client info. */
    private String message;

    /** The API path where the error occurred. */
    private String path;

    /**
     * Constructs a new {@code ErrorResponse} with all fields.
     *
     * @param timestamp timestamp when the error occurred
     * @param httpStatus HTTP status code of the error
     * @param errorType short description of the error type
     * @param errorMessage detailed message explaining the error
     * @param apiPath the API path where the error occurred
     */
    public ErrorResponse(final LocalDateTime timestamp, final Integer httpStatus,
                         final String errorType, final String errorMessage, final String apiPath) {
        this.localDateTime = timestamp;
        this.status = httpStatus;
        this.error = errorType;
        this.message = errorMessage;
        this.path = apiPath;
    }
}

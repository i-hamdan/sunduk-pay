package com.bxb.sunduk_pay.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for sending structured WebSocket error messages to clients.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketErrorResponse {

    /** Type or category of the error (e.g., ERROR, USER_NOT_FOUND). */
    private String type;

    /** Human-readable error message describing the issue. */
    private String message;

    /** Timestamp indicating when the error occurred. */
    private String timestamp;
}

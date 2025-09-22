package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when the request payload is invalid or cannot be processed.
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class InvalidPayloadException extends RuntimeException {
    public InvalidPayloadException(String message) {
        super(message);
    }
}

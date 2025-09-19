package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when a user session is invalid, expired, or unauthorized.
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class InvalidSessionException extends RuntimeException {
    public InvalidSessionException(String message) {
        super(message);
    }
}

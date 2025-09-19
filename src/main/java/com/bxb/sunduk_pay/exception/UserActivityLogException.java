package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when an error occurs while recording or processing
 * a user's activity log (e.g., login, signup, or other actions).
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class UserActivityLogException extends RuntimeException {
    public UserActivityLogException(String message) {
        super(message);
    }
}

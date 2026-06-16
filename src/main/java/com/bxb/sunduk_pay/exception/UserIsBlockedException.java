package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when a user is blocked.
 */
public class UserIsBlockedException extends RuntimeException {
    /**
     * Constructor for UserIsBlocked exception.
     * @param message
     */
    public UserIsBlockedException(final String message) {
        super(message);
    }
}

package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when a user is blocked.
 */
public class UserIsBlocked extends RuntimeException {
    /**
     * Constructor for UserIsBlocked exception.
     * @param message
     */
    public UserIsBlocked(String message) {
        super(message);
    }
}

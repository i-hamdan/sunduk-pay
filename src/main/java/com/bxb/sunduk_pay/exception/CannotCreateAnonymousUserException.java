package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when an anonymous user cannot be created.
 */
public class CannotCreateAnonymousUserException extends RuntimeException {
    /** Constructs a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public CannotCreateAnonymousUserException(final String message) {
        super(message);
    }
}

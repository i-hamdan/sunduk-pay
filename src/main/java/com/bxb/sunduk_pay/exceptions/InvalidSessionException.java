package com.bxb.sunduk_pay.exceptions;

/**
 * Exception thrown when a user session is invalid, expired, or unauthorized.
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class InvalidSessionException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidSessionException} with the specified detail message.
     *
     * @param msg the detail message explaining why the session is invalid
     */
    public InvalidSessionException(final String msg) {
        super(msg);
    }
}

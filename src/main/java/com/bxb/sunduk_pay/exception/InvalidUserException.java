package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when a user is invalid, unauthorized, or does not meet
 * the required criteria for an operation.
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class InvalidUserException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidUserException} with the specified
     * detail message.
     *
     * @param msg the detail message explaining
     * why the user is considered invalid
     */
    public InvalidUserException(final String msg) {
        super(msg);
    }
}

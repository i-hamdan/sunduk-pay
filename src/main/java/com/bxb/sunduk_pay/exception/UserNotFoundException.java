package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when a requested user cannot be found in the system.
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code UserNotFoundException} with the specified detail message.
     *
     * @param message the detail message explaining which user was not found
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}

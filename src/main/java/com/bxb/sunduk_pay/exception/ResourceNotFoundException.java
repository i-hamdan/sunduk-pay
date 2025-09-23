package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when a requested resource (e.g., user, wallet, transaction)
 * cannot be found in the system.
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class ResourceNotFoundException extends RuntimeException {
    /**
     * Constructs a new {@code ResourceNotFoundException} with the specified
     * detail message.
     *
     * @param message the detail message
     * explaining why the resource could not be found
     */
    public ResourceNotFoundException(final String message) {
        super(message);
    }
}

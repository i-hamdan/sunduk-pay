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
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

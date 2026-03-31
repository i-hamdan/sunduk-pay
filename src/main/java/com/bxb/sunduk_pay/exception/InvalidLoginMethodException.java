package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when an invalid login method is provided.
 */
public class InvalidLoginMethodException extends RuntimeException {
    /**
     * Constructs a new InvalidLoginMethodException with the specified detail
     * message.
     *
     * @param message the detail message
     */
    public InvalidLoginMethodException(String message) {
        super(message);
    }
}

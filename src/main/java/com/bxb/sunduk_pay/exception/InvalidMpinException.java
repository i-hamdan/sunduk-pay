package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when an invalid MPIN is provided.
 */
public class InvalidMpinException extends RuntimeException {
    /**
     * Constructs a new InvalidMpinException
     * with the specified detail message.
     *@param message
     */
    public InvalidMpinException(final String message) {
        super(message);
    }
}

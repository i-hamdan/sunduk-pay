package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when messages cannot be fetched from the database.
 */
public class CannotFetchMessagesException extends RuntimeException {
    /**
     * Constructs a new CannotFetchMessagesException
     * with the specified detail message.
     *
     * @param message the detail message
     */
    public CannotFetchMessagesException(final String message) {
        super(message);
    }
}

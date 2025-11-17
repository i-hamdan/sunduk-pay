package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when there is an error processing chat messages.
 */
public class ChatProcessingException extends RuntimeException {
    /**
     * Constructs a new ChatProcessingException
     * with the specified detail message.
     *
     * @param message the detail message
     */
    public ChatProcessingException(final String message) {
        super(message);
    }
}

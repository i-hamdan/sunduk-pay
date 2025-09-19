package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when an email cannot be sent successfully.
 * <p>
 * Extends {@link RuntimeException} and allows passing a custom message
 * describing the reason for the failure.
 * </p>
 */
public class EmailSendingException extends RuntimeException {
    /**
     * Constructs a new {@code EmailSendingException} with the specified detail message.
     *
     * @param message the detail message explaining why the email could not be sent
     */
    public EmailSendingException(String message) {
        super(message);
    }
}

package com.bxb.sunduk_pay.exceptions;

/**
 * Exception thrown when an error occurs while sending an SMS
 * (e.g., service failure, invalid number, or network issues).
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class SmsServiceException extends RuntimeException {

    /**
     * Constructs a new {@code SmsServiceException} with the specified detail message.
     *
     * @param message the detail message explaining the SMS sending failure
     */
    public SmsServiceException(final String message) {
        super(message);
    }
}

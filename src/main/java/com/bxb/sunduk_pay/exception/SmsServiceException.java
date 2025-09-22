package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when an error occurs while sending an SMS
 * (e.g., service failure, invalid number, or network issues).
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class SmsServiceException extends RuntimeException {
    public SmsServiceException(String message) {
        super(message);
    }
}

package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when there is an error while creating or processing
 * a Stripe payment session.
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class StripeSessionException extends RuntimeException {
    public StripeSessionException(String message) {
        super(message);
    }
}

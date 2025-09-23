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
    /**
     * Constructs a new StripeSessionException with
     * the specified detail message.
     *
     * @param message the detail message
     *                explaining the reason for the exception
     */
    public StripeSessionException(final String message) {
        super(message);
    }
}

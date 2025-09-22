package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when a requested transaction cannot be found in the system.
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class TransactionNotFoundException extends RuntimeException {

    /**
     * Constructs a new TransactionNotFoundException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public TransactionNotFoundException(final String message) {
        super(message);
    }
}

package com.bxb.sunduk_pay.exceptions;

/**
 * Exception thrown when an error occurs during the processing of a transaction.
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class TransactionProcessingException extends RuntimeException {

    /**
     * Constructs a new {@code TransactionProcessingException} with the specified detail message.
     *
     * @param message final detail message explaining the transaction processing failure
     */
    public TransactionProcessingException(final String message) {
        super(message);
    }
}

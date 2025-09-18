package com.bxb.sunduk_pay.exceptions;

/**
 * Exception thrown when a wallet or account has insufficient balance
 * to perform a requested transaction.
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class InsufficientBalanceException extends RuntimeException {

    /**
     * Constructs a new {@code InsufficientBalanceException} with the specified detail message.
     *
     * @param message final detail message explaining why the balance is insufficient
     */
    public InsufficientBalanceException(final String message) {
        super(message);
    }
}

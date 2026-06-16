package com.bxb.sunduk_pay.exception;


/**
 * Exception thrown when a wallet cannot be created due to business rules
 * or system constraints.
 * <p>
 * Extends {@link RuntimeException} and allows passing a custom message
 * describing the cause of the failure.
 * </p>
 */
public class CannotCreateWalletException extends RuntimeException {
    /**
     * Constructs a new {@code CannotCreateWalletException} with the
     * specified detail message.
     *
     * @param message the detail message explaining why the wallet
     *                could not be created
     */
    public CannotCreateWalletException(final String message) {
        super(message);
    }
}

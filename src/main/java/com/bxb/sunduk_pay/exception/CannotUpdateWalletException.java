package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when a wallet cannot be updated due to business rules
 * or system constraints.
 * <p>
 * Extends {@link RuntimeException} and allows passing a custom message
 * describing the reason for the failure.
 * </p>
 */
public class CannotUpdateWalletException extends RuntimeException {
    /**
     * Constructs a new {@code CannotUpdateWalletException} with the specified
     * detail message.
     *
     * @param message the detail message explaining why the wallet could not be updated
     */
    public CannotUpdateWalletException(final String message) {
        super(message);
    }
}

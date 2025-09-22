package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when a requested wallet (main or sub-wallet) cannot be found
 * in the system.
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class WalletNotFoundException extends RuntimeException {
    /**
     * Constructs a new {@code WalletNotFoundException} with the specified
     * detail message.
     *
     * @param message the detail message explaining why the wallet was not found
     */
    public WalletNotFoundException(final String message) {
        super(message);
    }
}

package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when attempting to create a sub-wallet
 * that already exists.
 */
public class SubWalletAlreadyExistsException extends
        RuntimeException {
    /**
     * Constructs a new SubWalletAlreadyExistsException
     * with the specified detail message.
     * @param message the detail message
     */
    public SubWalletAlreadyExistsException(
            final String message) {
        super(message);
    }
}

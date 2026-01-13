package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when an inactive global wallet is provided.
 */
public class InactiveGlobalWalletException extends RuntimeException {

    /**
     * Constructs a new InActiveGlobalWalletException
     * with the specified detail message.
     *@param message
     */
    public InactiveGlobalWalletException(final String message) {
        super(message);
    }
}

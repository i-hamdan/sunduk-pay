package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when an invalid or unsupported sub-wallet type is used
 * in a wallet-related operation.
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class InvalidSubWalletTypeException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidSubWalletTypeException} with the specified detail message.
     *
     * @param message the detail message explaining why the sub-wallet type is invalid
     */
    public InvalidSubWalletTypeException(String message) {
        super(message);
    }
}

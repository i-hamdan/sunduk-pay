package com.bxb.sunduk_pay.exceptions;

/**
 * Exception thrown when a user attempts to create more sub-wallets
 * than the allowed maximum limit.
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class MaxSubWalletsExceededException extends RuntimeException {

    /**
     * Constructs a new {@code MaxSubWalletsExceededException} with the specified detail message.
     *
     * @param msg the detail message explaining why the maximum sub-wallet limit was exceeded
     */
    public MaxSubWalletsExceededException(final String msg) {
        super(msg);
    }
}

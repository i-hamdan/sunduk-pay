package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when a user attempts to create more sub-wallets
 * than the allowed maximum limit.
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class MaxSubWalletsExceededException extends RuntimeException {
    public MaxSubWalletsExceededException(String message) {
        super(message);
    }
}

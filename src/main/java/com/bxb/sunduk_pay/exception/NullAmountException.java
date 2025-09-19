package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when a required transaction amount is null or missing.
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class NullAmountException extends RuntimeException {
    public NullAmountException(String message) {
        super(message);
    }
}

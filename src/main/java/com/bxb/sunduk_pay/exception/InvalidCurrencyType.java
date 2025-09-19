package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when an invalid or unsupported currency type is used
 * in a currency-related operation.
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class InvalidCurrencyType extends RuntimeException {
    public InvalidCurrencyType(String message) {
        super(message);
    }
}

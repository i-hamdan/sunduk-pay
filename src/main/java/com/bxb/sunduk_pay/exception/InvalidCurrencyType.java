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

    /**
     * Constructs a new {@code InvalidCurrencyType} exception with the specified detail message.
     *
     * @param message the detail message explaining why the currency type is invalid
     */
    public InvalidCurrencyType(String message) {
        super(message);
    }
}

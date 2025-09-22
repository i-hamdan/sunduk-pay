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
     * Constructs a new InvalidCurrencyType exception with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public InvalidCurrencyType(final String message) {
        super(message);
    }
}

package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when a custom exchange rate operation fails
 * or is invalid.
 * <p>
 * Extends {@link RuntimeException} and allows passing a custom message
 * describing the cause of the failure.
 * </p>
 */
public class CustomExchangeRateException extends RuntimeException {

    /**
     * Constructs a new {@code CustomExchangeRateException} with the specified detail message.
     *
     * @param message the detail message explaining the reason for the failure
     */
    public CustomExchangeRateException(String message) {
        super(message);
    }
}

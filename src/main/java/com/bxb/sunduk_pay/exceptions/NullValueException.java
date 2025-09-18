package com.bxb.sunduk_pay.exceptions;

/**
 * Exception thrown when a required field or value is null or missing.
 * <p>
 * Extends {@link RuntimeException} and allows specifying a custom message
 * describing the reason for the failure.
 * </p>
 */
public class NullValueException extends RuntimeException {

    /**
     * Constructs a new {@code NullValueException} with the specified detail message.
     *
     * @param message final detail message explaining which value was null or missing
     */
    public NullValueException(final String message) {
        super(message);
    }
}

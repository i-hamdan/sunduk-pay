package com.bxb.sunduk_pay.exception;

/**
 * Custom exception class for handling investment-related errors.
 */
public class InvestmentException extends RuntimeException {

    /**
     * Constructs a new InvestmentException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvestmentException(final String message) {
        super(message);
    }
}

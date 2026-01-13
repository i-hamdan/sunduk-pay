package com.bxb.sunduk_pay.exception;

public class GlobalPotNotFoundException extends RuntimeException {
    /**
     * Constructs a new {@code GlobalPotNotFoundException} with the specified
     * detail message.
     *
     * @param message the detail message explaining why
     *                the global pot was not found
     */
    public GlobalPotNotFoundException(final String message) {
        super(message);
    }
}

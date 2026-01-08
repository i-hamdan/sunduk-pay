package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when a global pot document is not found in the system.
 * <p>
 * Extends {@link RuntimeException} and allows passing a custom message
 * describing the reason for the document not being found.
 * </p>
 */
public class GlobalPotDocumentNotFoundException extends RuntimeException {
    /**
     * Constructs a new {@code GlobalPotDocumentNotFoundException} with the
     * specified detail message.
     *
     * @param message the detail message explaining why the global pot document
     *                was not found
     */
    public GlobalPotDocumentNotFoundException (final String message){
        super(message);
    }
}

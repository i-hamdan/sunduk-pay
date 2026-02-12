package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when a user attempts to access a resource
 * or perform an action for which they do not have the necessary permissions.
 */
public class AccessDeniedException extends RuntimeException {
    /**
     * Constructs a new AccessDeniedException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the access
     * denial
     */
    public AccessDeniedException(String message) {
        /**
         * Calls the constructor of the superclass (RuntimeException)
         * with the provided message.
         * This allows the exception to carry a descriptive message about the
         * access denial.
         */
        super(message);
    }
}

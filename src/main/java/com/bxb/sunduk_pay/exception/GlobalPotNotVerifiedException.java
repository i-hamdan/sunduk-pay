package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when a Global Pot is not verified but an operation
 * requiring verification is attempted.
 *
 * <p>This exception indicates that the requested action cannot proceed
 * because the Global Pot verification process has not been completed.</p>
 *
 * Example scenarios:
 * <ul>
 *     <li>User tries to perform an operation on an unverified Global Pot</li>
 *     <li>Verification status check fails before processing the request</li>
 * </ul>
 *
 */

public class GlobalPotNotVerifiedException extends RuntimeException{
    /**
     * Constructs a new GlobalPotNotVerifiedException with the specified detail message.
     *
     * @param message the detail message explaining why the exception occurred
     */
    public GlobalPotNotVerifiedException(String message) {
        super(message);
    }
}

package com.bxb.sunduk_pay.exception;

public class GoalAmountBelowThresholdException extends RuntimeException {
    /**
     * Constructs a new GoalAmountBelowThresholdException with the specified
     * detail message.
     *
     * @param message the detail message
     */
    public GoalAmountBelowThresholdException(final String message) {
        super(message);
    }
}

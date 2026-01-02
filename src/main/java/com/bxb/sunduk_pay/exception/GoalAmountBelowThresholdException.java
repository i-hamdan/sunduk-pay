package com.bxb.sunduk_pay.exception;

public class GoalAmountBelowThresholdException extends RuntimeException {
    public GoalAmountBelowThresholdException(String message) {
        super(message);
    }
}

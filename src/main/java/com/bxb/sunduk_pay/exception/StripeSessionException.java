package com.bxb.sunduk_pay.exception;

public class StripeSessionException extends RuntimeException {
    public StripeSessionException(String message) {
        super(message);
    }
}

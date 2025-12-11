package com.bxb.sunduk_pay.exception;

public class MpinAlreadyExists extends RuntimeException {
    public MpinAlreadyExists(String message) {
        super(message);
    }
}

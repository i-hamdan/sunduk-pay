package com.bxb.sunduk_pay.exception;

public class MpinAlreadyExists extends RuntimeException {
    /**
     * Exception indicating that an MPIN already exists.
     *
     * @param message Detailed message for the exception.
     */
    public MpinAlreadyExists(final String message) {
        super(message);
    }
}

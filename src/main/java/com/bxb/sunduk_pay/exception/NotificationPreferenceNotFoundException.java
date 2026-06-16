package com.bxb.sunduk_pay.exception;

/** Exception thrown when a notification preference is not found. */
public class NotificationPreferenceNotFoundException extends RuntimeException {
    /** Constructs a new NotificationPrefrenceNotFoundException with the
     *  specified detail message. */
    public NotificationPreferenceNotFoundException(String message) {
        super(message);
    }
}

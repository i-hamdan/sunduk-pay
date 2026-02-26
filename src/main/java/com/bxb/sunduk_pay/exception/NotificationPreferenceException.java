package com.bxb.sunduk_pay.exception;

/** Custom exception for handling errors related to notification preferences. */
public class NotificationPreferenceException extends RuntimeException {
  /** Constructs a new NotificationPreferenceException with the specified
   * detail message. */
    public NotificationPreferenceException(String message) {
        super(message);
    }
}

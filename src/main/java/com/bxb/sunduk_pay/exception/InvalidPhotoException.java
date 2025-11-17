package com.bxb.sunduk_pay.exception;

/**
 * Custom exception thrown when a photo operation
 * encounters an invalid photo.
 */
public class InvalidPhotoException extends RuntimeException {
    /**
     * Constructor for InvalidPhotoException.
     * @param message
     */
    public InvalidPhotoException(final String message) {
        super(message);
    }
}

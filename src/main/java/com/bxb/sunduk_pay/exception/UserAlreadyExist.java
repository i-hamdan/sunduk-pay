package com.bxb.sunduk_pay.exception;

public class UserAlreadyExist extends RuntimeException {
    /**
     * Constructs a new UserAlreadyExist exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public UserAlreadyExist(final String message) {
        super(message);
    }
}

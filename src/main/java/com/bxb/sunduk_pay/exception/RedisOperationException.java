package com.bxb.sunduk_pay.exception;

/** * Custom exception to indicate errors during Redis operations.
 */
public class RedisOperationException extends RuntimeException {
    /**
     * Constructs a new RedisOperationException
     * with the specified detail message.
     *
     * @param message the detail message
     */
    public RedisOperationException(String message) {
        super(message);
    }
}

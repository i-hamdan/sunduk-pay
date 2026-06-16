package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when a requested resource has expired
 * and is no longer available.
 */
public class ResourceExpiredException extends RuntimeException {
  /** Constructs a new ResourceExpiredException with the
   * specified detail message. */
    public ResourceExpiredException(String message) {
        super(message);
    }
}

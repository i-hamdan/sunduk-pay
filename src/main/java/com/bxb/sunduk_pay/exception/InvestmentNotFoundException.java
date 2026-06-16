package com.bxb.sunduk_pay.exception;

/**
 * Exception thrown when an investment is not found.
 */
public class InvestmentNotFoundException extends RuntimeException {
  /**
   * Constructs a new InvestmentNotFoundException
   * with the specified detail message.
   * @param message the detail message
   */
  public InvestmentNotFoundException(final String message) {
    super(message);
  }
}

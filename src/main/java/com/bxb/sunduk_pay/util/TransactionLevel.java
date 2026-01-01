package com.bxb.sunduk_pay.util;
/**
 * Represents the level or scope of a transaction within SundukPay.
 * <p>This enum distinguishes whether a transaction occurs
 * inside the user's ecosystem (internal transfers)
 * or with an external party such as a bank or another user.</p>
 */
public enum TransactionLevel {
  /** Transaction occurring within the user's own accounts or pots. */
    INTERNAL,
/** Transaction involving an external party, such as a bank or another user. */
    EXTERNAL,
    /** Transaction that has been fully processed and settled. */
    INVESTED,

    GLOBAL_POT
}

package com.bxb.sunduk_pay.util;

/**
 * Represents the level or scope of a transaction within SundukPay.
 *
 * <p>This enum distinguishes whether a transaction occurs
 * inside the user's ecosystem (internal transfers)
 * or with an external party such as a bank or another user.</p>
 */
public enum TransactionLevel {

    /**
     * Internal transaction within the user's ecosystem
     * (e.g., main wallet ↔ sub-wallet or sub-wallet ↔ sub-wallet).
     */
    INTERNAL,

    /**
     * External transaction involving parties outside the user's ecosystem
     * (e.g., bank transfers, payments to other users, card/UPI payments).
     */
    EXTERNAL
}

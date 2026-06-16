package com.bxb.sunduk_pay.util;

/**
 * Represents the type of a financial transaction in SundukPay.
 * <p>This enum indicates whether funds are coming into (CREDIT)
 * <p>This enum indicates whether
 * funds are coming into (CREDIT)
 * or going out of (DEBIT) a wallet.</p>
 */
public enum TransactionType {
    /** Transaction type indicating funds are added to a wallet. */
    CREDIT,
    /** Transaction type indicating funds are removed from a wallet. */
    DEBIT
}

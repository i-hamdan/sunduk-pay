package com.bxb.sunduk_pay.util;

/**
 * Represents the type of a financial transaction in SundukPay.
 *
 * <p>This enum indicates whether funds are coming into (CREDIT)
 * or going out of (DEBIT) a wallet.</p>
 */
public enum TransactionType {

    /**
     * Money credited to a wallet (e.g., deposits, received payments).
     */
    CREDIT,

    /**
     * Money debited from a wallet (e.g., withdrawals, sent payments).
     */
    DEBIT
}

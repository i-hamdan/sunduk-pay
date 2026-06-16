package com.bxb.sunduk_pay.util;

/**
 * Enumeration representing different types of
 * transaction fetching options.
 */
public enum FetchTransactionType {
    /**
     * Fetch only credit transactions.
     */
    CREDIT,
    /**
     * Fetch only debit transactions.
     */
    DEBIT,
    /**
     * Fetch all transactions.
     */
    ALL
}

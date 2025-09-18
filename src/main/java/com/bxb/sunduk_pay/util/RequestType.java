package com.bxb.sunduk_pay.util;

/**
 * Represents the type of operation or request that can be
 * performed on a wallet or transaction in SundukPay.
 *
 * <p>Used to categorize API requests or service calls
 * for better routing and handling.</p>
 */
public enum RequestType {

    /**
     * Create a new resource (e.g., create a wallet).
     */
    CREATE,

    /**
     * Update an existing resource (e.g., update wallet details).
     */
    UPDATE,

    /**
     * Delete an existing resource (e.g., delete a wallet).
     */
    DELETE,

    /**
     * Fetch transaction history or details.
     */
    FETCH_TRANSACTIONS,

    /**
     * Fetch wallet details.
     */
    FETCH_WALLET,

    /**
     * Transfer money between wallets or accounts.
     */
    TRANSFER_MONEY
}

package com.bxb.sunduk_pay.util;

/**
 * Defines the type of investment data to be fetched.
 * <p>
 * This enum is used to control whether investment data should be
 * retrieved for a specific pot or across all available investments.
 * </p>
 */
public enum InvestmentsFetchType {

    /**
     * Fetches investments related to a specific pot.
     */
    POT_INVESTMENTS,

    /**
     * Fetches all investments associated with the user.
     */
    ALL_INVESTMENTS
}

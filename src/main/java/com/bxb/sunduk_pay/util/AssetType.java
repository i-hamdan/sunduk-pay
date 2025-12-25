package com.bxb.sunduk_pay.util;

/**
 * Represents the type of asset involved in an investment or portfolio.
 * <p>
 * This enum is used to categorize assets for valuation, risk assessment,
 * and reporting purposes.
 * </p>
 */
public enum AssetType {

    /**
     * Equity-based assets representing ownership in a company.
     */
    STOCK,

    /**
     * Cash or cash-equivalent assets with high liquidity.
     */
    CASH,

    /**
     * Precious metal assets, primarily gold, used as
     * a store of value and hedge against inflation.
     */
    GOLD,

    /**
     * Islamic financial certificates representing
     * ownership in tangible assets or projects,
     * compliant with Shariah principles.
     */
    SUKUK
}

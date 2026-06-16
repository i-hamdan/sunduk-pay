
package com.bxb.sunduk_pay.util;

/**
 * Enum representing different time series options
 * for fetching past currency rates.
 */
public enum TimeSeries {
    /** Fetch rates for the past week. */
    WEEK,
    /** Fetch rates for the past month. */
    MONTH,
    /** Fetch rates for the past year. */
    YEAR,
    /** Fetch all available historical rates. */
    ALL
}

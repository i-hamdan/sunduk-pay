package com.bxb.sunduk_pay.util;

/**
 * Enumeration representing different time-series ranges
 * for analytics or reporting within SundukPay.
 *
 * <p>These values are typically used when filtering or aggregating
 * transaction data over a specific period.</p>
 */
public enum TimeSeries {

    /** Represents data aggregated over the last week. */
    WEEK,

    /** Represents data aggregated over the last month. */
    MONTH,

    /** Represents data aggregated over the last year. */
    YEAR,

    /** Represents all-time data (no time restriction). */
    ALL
}

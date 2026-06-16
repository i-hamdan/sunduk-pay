package com.bxb.sunduk_pay.util;

/**
 * Represents the type of request being performed on investments.
 * <p>
 * This enum is used to determine the action to be executed
 * in investment-related workflows.
 * </p>
 */
public enum InvestmentRequestType {

    /**
     * Creates a new investment.
     */
    CREATE_INVESTMENT,

    /**
     * Cancels an existing investment.
     */
    CANCEL_INVESTMENT,

    /**
     * Fetches investment details based on the request parameters.
     */
    FETCH_INVESTMENTS,

    /**
     * Updates the risk level associated with an investment.
     */
    CHANGE_RISK_LEVEL
}

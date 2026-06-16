package com.bxb.sunduk_pay.util;

/**
 * Represents the category of emails sent by the system.
 * <p>
 * This enum is used to classify emails based on their purpose,
 * helping determine templates, content, and handling logic.
 * </p>
 */
public enum EmailCategory {

    /**
     * Emails related to user onboarding and greetings.
     */
    WELCOME,

    /**
     * Emails related to security actions such as
     * authentication, password changes, or alerts.
     */
    SECURITY,

    /**
     * Emails related to financial or account transactions.
     */
    TRANSACTION
}

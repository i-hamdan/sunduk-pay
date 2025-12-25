package com.bxb.sunduk_pay.util;

/**
 * Represents the priority or urgency level of a case requirement.
 * <p>
 * This enum is used to categorize cases based on how quickly
 * they need to be addressed.
 * </p>
 */
public enum CaseRequirementType {

    /**
     * Normal priority cases with standard handling timelines.
     */
    NORMAL,

    /**
     * Urgent cases that require faster attention than normal cases.
     */
    URGENT,

    /**
     * Critical cases that demand immediate attention
     * due to high impact or risk.
     */
    CRITICAL
}

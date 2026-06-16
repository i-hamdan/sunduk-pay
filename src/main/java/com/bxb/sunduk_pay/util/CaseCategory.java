package com.bxb.sunduk_pay.util;

/**
 * Represents the category under which a case or cause is classified.
 * <p>
 * This enum is used to organize and filter cases based on their nature
 * or purpose.
 * </p>
 */
public enum CaseCategory {

    /**
     * Cases related to medical treatment, healthcare, or emergencies.
     */
    MEDICAL,

    /**
     * Cases related to education, including fees, resources,
     * or academic support.
     */
    EDUCATION,

    /**
     * Cases related to urgent or unforeseen emergency situations.
     */
    EMERGENCY,

    /**
     * Community-driven cases aimed at social welfare
     * or collective causes.
     */
    COMMUNITY,

    /**
     * Personal cases raised for individual needs or support.
     */
    PERSONAL,

    /**
     * Represents all case categories.
     * Typically used for filtering or fetching cases without
     * category-based restrictions.*/
    ALL,

    /**
     * Cases related to food assistance and hunger relief.
     */
    FOOD_AND_HUNGER,

    /*** Cases related to charitable causes and philanthropy. */
    CHARITY
}

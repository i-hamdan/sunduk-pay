package com.bxb.sunduk_pay.util;

/**
 * Represents the visibility scope of a Pot.
 * <p>
 * The scope determines who can view or access a pot.
 * </p>
 */
public enum PotScope {

    /**
     * Private scope indicates that the pot is accessible
     * only to the owner or explicitly authorized users.
     */
    PRIVATE,

    /**
     * Public scope indicates that the pot is visible
     * and accessible to all users.
     */
    PUBLIC
}

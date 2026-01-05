package com.bxb.sunduk_pay.util;
/**
 * Enum representing the status of a pot.
 */
public enum PotStatus {
    /** Pot is active. */
    PENDING_VERIFICATION,
    /** Pot is currently on hold. */
    ON_HOLD,
    /** Pot has been closed. */
    CLOSED,
    /** Pot has been verified. */
    VERIFIED,
    /** Pot has been rejected. */
    REJECTED
}

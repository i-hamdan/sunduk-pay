package com.bxb.sunduk_pay.util;

/**
 * Enum representing different types of MPIN requests.
 */
public enum MpinRequestType {
    /**
     * Request to set a new MPIN.
     */
    SET_MPIN,
    /**
     * Request to change an existing MPIN.
     */
    OTP,
    /**
     * Request to validate an OTP.
     */
    VALIDATE_OTP,
    /**
     * Request to reset MPIN when forgotten.
     */
    FORGOT_MPIN_RESET,
    /**
     * Request to reset MPIN.
     */
    RESET_MPIN;
}

package com.bxb.sunduk_pay.util;

public enum AdminRequestType {

    /**
     * Verifies a global pot for authenticity or compliance.
     */
    VERIFY_POT,

    /**
     * Request type used for document verification.
     */
    VERIFY_DOCUMENT,

    /**
     * Creates a new global pot.
     */
    CREATE_POT;
}

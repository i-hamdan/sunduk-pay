package com.bxb.sunduk_pay.encryption;
/**
 * Interface defining MPIN validation methods.
 */

public interface MpinValidations {
    /**
     * Validates the MPIN for payment operations.;
     * @param uuid
     * @param inputMpin
     */
    void validateMpinForPayment(final String uuid, final String inputMpin);

}

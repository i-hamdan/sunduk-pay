package com.bxb.sunduk_pay.encryption;

import com.bxb.sunduk_pay.model.Mpin;

/**
 * Interface defining MPIN validation methods.
 */

public interface MpinValidations {
    /**
     * Validates the MPIN for payment operations.;
     * @param uuid
     * @param inputMpin
     */
    void validateMpin(final String uuid, final String inputMpin);
/**
     * Validates the MPIN for setting a new MPIN.
     * @param uuid
     */
    Mpin findMpinByUuid(final String uuid);


}

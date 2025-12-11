package com.bxb.sunduk_pay.encryption;

import com.bxb.sunduk_pay.model.Mpin;
import com.bxb.sunduk_pay.model.User;

/**
 * Interface defining MPIN validation methods.
 */

public interface MpinValidations {
    /**
     * Validates the MPIN for payment operations.;
     * @param uuid
     * @param inputMpin
     */
    void validateMpin(String uuid, String inputMpin);
     /**
     * Validates the MPIN for setting a new MPIN.
     * @param uuid
     * @return Mpin
     */
    Mpin findMpinByUuid(String uuid);


   /**
   * Retrieves user email information by UUID.
   *
   * @param email user UUID
   * @return {@link User
    **/
    User getUserEmailInfo(String email);



    void mpinIsExists(String uuid);

}

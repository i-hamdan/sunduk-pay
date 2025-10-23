package com.bxb.sunduk_pay.encryption;

import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class MpinEncryption {
    /**
     * Cost factor for BCrypt hashing.
     */
    private static final int COST_FACTOR = 12;

    /**
     * Encrypts the given MPIN using BCrypt hashing.
     *
     * @param mpin
     * @return The hashed MPIN.
     */
    public String encryptMpin(String mpin) {
        if (mpin == null) {
            throw new ResourceNotFoundException("MPIN cannot be null");
        }

        return BCrypt.hashpw(mpin, BCrypt.gensalt(COST_FACTOR));
    }

/**
     * Verifies the given MPIN against the stored hashed MPIN.
     *
     * @param inputMpin The MPIN to verify.
     * @param hashedMpin The stored hashed MPIN.
     * @return true if the MPINs match, false otherwise.
     */

    public boolean verifyMpin(String inputMpin, String hashedMpin) {
        if (inputMpin == null) {
            throw new ResourceNotFoundException("MPIN cannot be null");
        }
        return BCrypt.checkpw(inputMpin, hashedMpin);
    }

}

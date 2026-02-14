package com.bxb.sunduk_pay.encryption;

import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

/**
 * Utility class for encrypting and verifying MPINs using BCrypt hashing.
 */
@Log4j2
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
    public String encryptMpin(final String mpin) {
        if (mpin == null) {
            throw new ResourceNotFoundException("MPIN cannot be null");
        }
        long startTime = System.currentTimeMillis();
        log.info("Before Encryption MPIN for user : 0ms");
        
        String hashpw = BCrypt.hashpw(mpin, BCrypt.gensalt(COST_FACTOR));
        
        long endTime = System.currentTimeMillis();
        log.info("After Encryption MPIN for user : {}ms"
                , (endTime - startTime));
        
        return hashpw;
    }

/**
     * Verifies the given MPIN against the stored hashed MPIN.
     *
     * @param inputMpin The MPIN to verify.
     * @param hashedMpin The stored hashed MPIN.
     * @return true if the MPINs match, false otherwise.
     */

    public boolean verifyMpin(final String inputMpin,
                              final String hashedMpin) {
        if (inputMpin == null) {
            throw new ResourceNotFoundException("MPIN cannot be null");
        }
        long startTime = System.currentTimeMillis();
        log.info("Before Compare MPIN for user : 0ms");
        
        boolean checkpw = BCrypt.checkpw(inputMpin, hashedMpin);
        
        long endTime = System.currentTimeMillis();
        log.info("After Compare MPIN for user : {}ms",
                endTime - startTime);
        return checkpw;
    }

}

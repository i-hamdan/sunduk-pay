package com.bxb.sunduk_pay.encryption;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Utility class for hashing operations.
 */
@Component
public class HashUtil {
    /**
     * Computes the SHA-256 hash of the given input string.
     *
     * @param input the input string to hash
     * @return the SHA-256 hash as a hexadecimal string
     */
    public String sha256(final String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] encodedHash = digest.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error computing SHA-256 hash", e);
        }
    }
}

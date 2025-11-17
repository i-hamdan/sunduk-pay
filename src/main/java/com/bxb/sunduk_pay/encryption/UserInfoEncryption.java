package com.bxb.sunduk_pay.encryption;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

/**
 * Utility class for encrypting and decrypting user information.
 */

@Component
public  final class  UserInfoEncryption {
/**  * Key and salt for encryption.
 */
    private static final String KEY = "mySecretKey123";
   /**  * Salt for encryption.
     */
    private static final String SALT = "a1b2c3d4e5f60708";
/**  * TextEncryptor instance for encryption and decryption.
     */
    private static final TextEncryptor ENCRYPTOR =
            Encryptors.text(KEY, SALT);

    /**
     * Encrypts the given field.
     * @param field
     * @return encrypted string
     */
    public  String encrypt(final String field) {
        if (field == null) {
            return null;
        }
        return ENCRYPTOR.encrypt(field);
    }

    /**
     * Decrypts the given encrypted field.
     * @param encryptedfiled
     * @return decrypted string
     */

    public  String decrypt(final String encryptedfiled) {
        if (encryptedfiled == null) {
            return null;
        }
        return ENCRYPTOR.decrypt(encryptedfiled);
    }

}

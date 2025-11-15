package com.bxb.sunduk_pay.encryption;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component
        ;

@Component
public class UserInfoEncryption {

    private static final String KEY = "mySecretKey123";
    private static final String SALT = "a1b2c3d4e5f60708";

    private static final TextEncryptor ENCRYPTOR =
            Encryptors.text(KEY, SALT);


    public  String encrypt(String field) {
        if (field == null) {
            return null;
        }
        return ENCRYPTOR.encrypt(field);
    }


    public  String decrypt(String encryptedfiled) {
        if (encryptedfiled == null) {
            return null;
        }
        return ENCRYPTOR.decrypt(encryptedfiled);
    }

}

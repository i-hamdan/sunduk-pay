package com.bxb.sunduk_pay.util;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GenerateKeyUtilTest {

    @Autowired
    GenerateKeyUtil generateKeyUtil;
    @Test
void test(){
        String key = generateKeyUtil.generateTransactionKey("16189578-6277-4702-8e0b-37f9e18543a0",
                "+91 82695 27774");
        System.out.println(key);
    }

}
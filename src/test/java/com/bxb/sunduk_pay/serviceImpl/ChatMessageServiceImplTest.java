package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.service.ChatMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChatMessageServiceImplTest {

    @Autowired
    ChatMessageService  messageService;
    @Test
    void test(){
        assertNotNull(messageService.getReceiverUserDetails("+91 92852 53980"));
    }
}
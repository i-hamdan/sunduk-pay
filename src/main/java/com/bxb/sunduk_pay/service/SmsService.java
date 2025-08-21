package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;

public interface SmsService {
    void sendSms(String to,String message);
    void processSmsEvent(TransactionEvent event);
}

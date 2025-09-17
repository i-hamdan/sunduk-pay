package com.bxb.sunduk_pay.kafkaListeners;

import com.bxb.sunduk_pay.exception.SmsServiceException;
import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.service.SmsService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SmsListener {
private final SmsService smsService;

    public SmsListener(SmsService smsService) {
        this.smsService = smsService;
    }

    @KafkaListener(topics = "transaction-topic", groupId = "sms-service-group",concurrency = "3")
    public void consumeTransactionEvent(TransactionEvent transactionEvent){
        smsService.processSmsEvent(transactionEvent);
        }

    }


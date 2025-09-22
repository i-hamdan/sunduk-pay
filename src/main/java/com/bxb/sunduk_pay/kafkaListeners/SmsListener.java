package com.bxb.sunduk_pay.kafkaListeners;

import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.service.SmsService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener for processing transaction events and
 * sending SMS notifications.
 */
@Component
public class SmsListener {

    /**
     * Service for handling SMS notifications.
     */
    private final SmsService smsService;

    /**
     * Constructs the SmsListener with the given SmsService.
     *
     * @param smsService the SMS service to process events
     */
    public SmsListener(final SmsService smsService) {
        this.smsService = smsService;
    }

    /**
     * Listens to the "transaction-topic" Kafka topic for
     * transaction events.
     * Processes each event by invoking the SmsService
     * to handle SMS notifications.
     *
     * @param transactionEvent the transaction event received from Kafka
     */
    @KafkaListener(topics = "transaction-topic",
            groupId = "sms-service-group",
            concurrency = "3")
    public void consumeTransactionEvent(final TransactionEvent transactionEvent) {
        smsService.processSmsEvent(transactionEvent);
    }

}


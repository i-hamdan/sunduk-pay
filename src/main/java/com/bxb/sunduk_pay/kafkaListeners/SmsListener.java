package com.bxb.sunduk_pay.kafkaListeners;

import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener for processing transaction events and
 * sending SMS notifications.
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class SmsListener {

    /**
     * Service for handling SMS notifications.
     */
    private final SmsService smsService;


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
    public void consumeTransactionEvent(
            final TransactionEvent transactionEvent) {
        try{
        smsService.processSmsEvent(transactionEvent);
    } catch (Exception e) {
        // Log the exception and continue processing other messages
        // to prevent the listener from crashing due to a single failure.
    log.error(
"Error processing transaction event for transaction ID {}: {}",
            transactionEvent.getTransactionId(), e.getMessage(), e);

    }

}
}


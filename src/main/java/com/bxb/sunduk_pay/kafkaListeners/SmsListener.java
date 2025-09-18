package com.bxb.sunduk_pay.kafkaListeners;

import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.service.SmsService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener that consumes transaction events
 * and delegates SMS processing to {@link SmsService}.
 */
@Component
public class SmsListener {  // Made class final (DesignForExtension fix)

    /** Service used for sending SMS notifications. */
    private final SmsService smsService;

    /**
     * Constructs a new {@code SmsListener}.
     *
     * @param smsService the SMS service to handle transaction events
     */
    public SmsListener(final SmsService smsService) {
        this.smsService = smsService;
    }

    /**
     * Consumes a {@link TransactionEvent} from Kafka and delegates it
     * to the {@link SmsService} for processing.
     *
     * @param transactionEvent the transaction event (never {@code null})
     */
    @KafkaListener(
            topics = "transaction-topic",
            groupId = "sms-service-group",
            concurrency = "3"
    )
    public void consumeTransactionEvent(final TransactionEvent transactionEvent) {
        // Delegate to SMS service
        smsService.processSmsEvent(transactionEvent);
    }
}

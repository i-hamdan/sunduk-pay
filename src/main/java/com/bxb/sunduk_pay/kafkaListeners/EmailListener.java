package com.bxb.sunduk_pay.kafkaListeners;

import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener for processing email-related events.
 * Listens to the "user-topic" Kafka topic and delegates
 * email event processing to the EmailService.
 */
@Component
@RequiredArgsConstructor
public class EmailListener {
    /** Service for handling email operations. */
    private final EmailService emailService;
    /**
     * Consumes user-related Kafka events and processes them
     * for email notifications.
     *
     * @param userKafkaEvent the user Kafka
     *event containing email details
     */
    @KafkaListener(topics = "user-topic",
            groupId = "email-service-group",
            concurrency = "3")
    public void consumeEmailEvent(final UserKafkaEvent userKafkaEvent) {
        emailService.processEmailEvent(userKafkaEvent);
    }
}

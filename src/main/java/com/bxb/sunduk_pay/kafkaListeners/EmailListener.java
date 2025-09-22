package com.bxb.sunduk_pay.kafkaListeners;

import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener for processing email-related events.
 * Listens to the "user-topic" Kafka topic and delegates
 * email event processing to the EmailService.
 */
@Component
public class EmailListener {

    /** Service for handling email operations */
    private final EmailService emailService;

    /**
     * Constructor for EmailListener.
     *
     * @param emailService the email service to process email events
     */
    public EmailListener(final EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Consumes user-related Kafka events and processes them
     * for email notifications.
     *
     * @param userKafkaEvent the user Kafka event containing email details
     */
    @KafkaListener(topics = "user-topic",
            groupId = "email-service-group",
            concurrency = "3")
    public void consumeEmailEvent(final UserKafkaEvent userKafkaEvent) {
        emailService.processEmailEvent(userKafkaEvent);
    }
}

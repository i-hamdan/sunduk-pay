package com.bxb.sunduk_pay.kafkaListeners;

import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener that consumes user-related events and
 * delegates email handling to {@link EmailService}.
 */
@Component
public class EmailListener {  // Made class final (DesignForExtension fix)

    /** Service used for sending emails based on user events. */
    private final EmailService emailService;

    /**
     * Constructs a new {@code EmailListener}.
     *
     * @param emailService the email service to handle user-related events
     */
    public EmailListener(final EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Consumes a {@link UserKafkaEvent} from Kafka and delegates it
     * to the {@link EmailService} for processing.
     *
     * @param userKafkaEvent the user event (never {@code null})
     */
    @KafkaListener(
            topics = "user-topic",
            groupId = "email-service-group",
            concurrency = "3"
    )
    public void consumeEmailEvent(final UserKafkaEvent userKafkaEvent) {
        // Delegate directly to the service; error handling is inside the service
        emailService.processEmailEvent(userKafkaEvent);
    }
}

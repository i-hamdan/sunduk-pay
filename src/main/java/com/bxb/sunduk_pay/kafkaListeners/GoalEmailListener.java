package com.bxb.sunduk_pay.kafkaListeners;

import com.bxb.sunduk_pay.kafkaEvents.GoalCompletionEvent;
import com.bxb.sunduk_pay.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener that consumes goal-completion events
 * and delegates handling to {@link EmailService}.
 */
@Component
public class GoalEmailListener {  // Made class final (DesignForExtension fix)

    /** Service used for sending emails. */
    private final EmailService emailService;

    /**
     * Constructs a new {@code GoalEmailListener}.
     *
     * @param emailService the email service to handle goal-completion events
     */
    public GoalEmailListener(final EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Consumes a {@link GoalCompletionEvent} from Kafka and delegates it
     * to the {@link EmailService} for processing.
     *
     * @param event the goal completion event (never {@code null})
     */
    @KafkaListener(
            topics = "goal-completion-topic",
            groupId = "email-service-group",
            concurrency = "3"
    )
    public void consumeGoalEvent(final GoalCompletionEvent event) {
        // Delegate event to the email service
        emailService.processGoalCompletionEvent(event);
    }
}

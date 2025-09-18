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
public class GoalEmailListener {

    private final EmailService emailService;

    public GoalEmailListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(
            topics = "goal-completion-topic",
            groupId = "email-service-group",
            concurrency = "3"
    )
    public void consumeGoalEvent(GoalCompletionEvent event) {
        // Delegate event to the email service
        emailService.processGoalCompletionEvent(event);
    }
}

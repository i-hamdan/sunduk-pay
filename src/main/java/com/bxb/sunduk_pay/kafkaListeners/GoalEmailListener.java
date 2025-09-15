package com.bxb.sunduk_pay.kafkaListeners;

import com.bxb.sunduk_pay.kafkaEvents.GoalCompletionEvent;
import com.bxb.sunduk_pay.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class GoalEmailListener {
    private final EmailService emailService;

    public GoalEmailListener(EmailService emailService) {
        this.emailService = emailService;
    }
    @KafkaListener(topics = "goal-completion-topic", groupId = "email-service-group", concurrency = "3")
    public void consumeGoalEvent(GoalCompletionEvent event) {
        emailService.processGoalCompletionEvent(event);
    }
}


package com.bxb.sunduk_pay.kafkaListeners;

import com.bxb.sunduk_pay.exception.EmailSendingException;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EmailListener {
    private final EmailService emailService;

    public EmailListener(EmailService emailService) {
        this.emailService = emailService;
    }
    @KafkaListener(topics = "user-topic", groupId = "email-service-group", concurrency = "3")
    public void consumeEmailEvent(UserKafkaEvent userKafkaEvent) {
       emailService.processEmailEvent(userKafkaEvent);
    }
}

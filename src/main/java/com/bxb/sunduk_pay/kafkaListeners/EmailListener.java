package com.bxb.sunduk_pay.kafkaListeners;

import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.service.EmailService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener for processing email-related events.
 * Listens to the "user-topic" Kafka topic and delegates
 * email event processing to the EmailService.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class EmailListener {
    /**
     * Service for handling email operations.
     */
    private final EmailService emailService;

    /**
     * Initializes the EmailListener bean
     * and logs its successful creation.
     */
    @PostConstruct
    public void init() {
        log.info("Email KafkaListener bean initialized successfully");
    }

    /**
     * Consumes user-related Kafka events and processes them
     * for email notifications.
     *
     * @param userKafkaEvent the user Kafka
     *                       event containing email details
     */
    @KafkaListener(topics = "user-topic", groupId = "email-service-group")
    public void consumeEmailEvent(final UserKafkaEvent userKafkaEvent) {
        log.info("Received email event for user: {}", userKafkaEvent.getEmail());
        try {
            emailService.processEmailEvent(userKafkaEvent);
        } catch (Exception ex) {
            log.error("Error occurred while processing email event for user: " + "{}", userKafkaEvent.getEmail(), ex);
        }
    }

        @KafkaListener(topics = "otp-email-topic", groupId = "otp-service" +
                "-group")
        public void consumeOtpEvent (final OtpEvent otpEvent){
            log.info("Received otp event for user: {}", otpEvent.getEmail());
            emailService.processOtpEvent(otpEvent);
        }
    }


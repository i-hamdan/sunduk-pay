package com.bxb.sunduk_pay.kafkaListeners;

import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;
import com.bxb.sunduk_pay.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class OtpListener {
    /**
     * Service for handling email operations.
     */
    private final EmailService emailService;

    /**
     * Consumes OTP-related Kafka events and processes them
     * for email notifications.
     * @param otpEvent
     */
    @KafkaListener(topics = "otp-topic",
        groupId = "otp-service-group")
    public void consumeOtpEvent(final OtpEvent otpEvent) {
        log.info("Received OTP event: {}", otpEvent);
        emailService.processOtpEvent(otpEvent);
    }


}

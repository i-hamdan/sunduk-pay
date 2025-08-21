package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;

public interface EmailService {
    void processEmailEvent(UserKafkaEvent event);
    void sendEmail(String to, String subject, String body);
}

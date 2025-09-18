package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.kafkaEvents.GoalCompletionEvent;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;

/**
 * Service interface for handling email-related operations.
 */
public interface EmailService {

    /**
     * Processes a user-related email event.
     *
     * @param event the user Kafka event containing user information
     */
    void processEmailEvent(UserKafkaEvent event);

    /**
     * Sends an email with the specified subject
     * and body to the given recipient.
     *
     * @param to      recipient email address
     * @param subject subject of the email
     * @param body    body content of the email
     */
    void sendEmail(String to, String subject, String body);

    /**
     * Processes a goal completion event and sends relevant notifications.
     *
     * @param event the goal completion Kafka event
     */
    void processGoalCompletionEvent(GoalCompletionEvent event);
}

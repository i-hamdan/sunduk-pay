package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.kafkaEvents.GoalCompletionEvent;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;

/**
 * Service interface for handling email-related operations.
 */
public interface EmailService {

    /**
     * Processes a user-related Kafka event to send an email.
     *
     * @param event the user Kafka event containing email details
     */
    void processEmailEvent(UserKafkaEvent event);

    /**
     * Sends an email to the specified recipient with the given subject and body.
     *
     * @param to      the recipient's email address
     * @param subject the subject of the email
     * @param body    the body content of the email
     */
    void sendEmail(String to, String subject, String body);

    /**
     * Processes a goal completion event to send a notification email.
     *
     * @param event the goal completion event containing relevant details
     */
    void processGoalCompletionEvent(GoalCompletionEvent event);
}

package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;
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
     * Sends an email to the specified
     * recipient with the given subject and body.
     * @param to      the recipient's email address
     * @param subject the subject of the email
     * @param body    the body content of the email
     *@param isHtml  indicates if the body is in HTML format
     */
    void sendEmail(String to,String from, String subject, String body,
                   Boolean isHtml);
/**
     * Processes an OTP-related Kafka event to send an email.
     *
     * @param event the OTP Kafka event containing email details
     */

    void processOtpEvent(OtpEvent event);

}

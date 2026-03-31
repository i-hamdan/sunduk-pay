package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;
import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;

/**
 * Service interface for sending SMS notifications.
 */
public interface SmsService {

    /**
     * Sends an SMS message to a specified phone number.
     *
     * @param to The recipient's phone number.
     * @param message The message content to be sent.
     */
    void sendSms(String to, String message);

    /**
     * Processes a transaction event and sends an
     * SMS notification based on the event details.
     *
     * @param event The transaction event
     *containing details for the SMS notification.
     */
    void processSmsEvent(TransactionEvent event);

    void processOtpEvent(OtpEvent otpEvent);
}

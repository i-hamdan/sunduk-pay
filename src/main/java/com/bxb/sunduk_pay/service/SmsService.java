package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;

/**
 * Service interface for sending SMS and processing SMS-related events.
 */
public interface SmsService {

    /**
     * Sends an SMS message to the specified recipient.
     *
     * @param to      the recipient's phone number
     * @param message the message content
     */
    void sendSms(String to, String message);

    /**
     * Processes an SMS event triggered by a transaction.
     *
     * @param event the transaction event containing SMS details
     */
    void processSmsEvent(TransactionEvent event);
}

package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.model.Reminder;

/**
 * Service interface for handling auto-pay confirmation processes.
 */
public interface AutoPayConfirmationService {

    /**
     * Creates a confirmation request for a high-value auto-pay reminder.
     *
     * @param reminder the reminder requiring confirmation
     */
    void createConfirmation(Reminder reminder);

    /**
     * Confirms an auto-pay request and triggers payment.
     *
     * @param confirmationId confirmation id
     * @param userId user id performing action
     */
    void confirmPayment(String confirmationId, String userId);

    /**
     * Rejects an auto-pay request.
     *
     * @param confirmationId confirmation id
     * @param userId user id performing action
     */
    void rejectPayment(String confirmationId, String userId);
}

package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.model.Reminder;

/** * Service interface for handling auto-payment processing based on reminders.
 */
public interface AutoPaymentService {

    /**
     * Processes an auto-payment based on the provided reminder.
     *
     * @param reminder the reminder containing details for the auto-payment
     */
    void processAutoPayment(Reminder reminder);

}

package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.util.NotificationType;

/** Service interface for sending notifications to users based on their
 * preferences. */
public interface NotificationSenderService {
    /** Sends a notification to the specified user based on the provided
     * notification type.
     *
     * @param user The user to whom the notification will be sent.
     * @param type The type of notification to be sent.
     */
    void send(
            final User user,
            final NotificationType type);
}

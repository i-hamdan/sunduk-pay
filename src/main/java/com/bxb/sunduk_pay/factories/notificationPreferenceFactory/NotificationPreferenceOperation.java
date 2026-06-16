package com.bxb.sunduk_pay.factories.notificationPreferenceFactory;

import com.bxb.sunduk_pay.request.NotificationPreferenceRequest;
import com.bxb.sunduk_pay.response.NotificationPreferenceResponse;
import com.bxb.sunduk_pay.util.NotificationPreferenceRequestType;

/** * Interface for operations related to notification preferences.
 */
public interface NotificationPreferenceOperation {
    /** Executes the notification preference operation. */
    NotificationPreferenceRequestType getPreferenceRequestType();

    /** Performs the notification preference operation based on the provided request. */
    NotificationPreferenceResponse perform(NotificationPreferenceRequest request);
}

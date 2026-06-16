package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.NotificationPreferenceRequestType;
import lombok.Getter;
import lombok.Setter;

/** * Request class for handling notification preference operations. */
@Getter
@Setter
public class NotificationPreferenceRequest {

    /** * The unique identifier of the user for whom the
     * notification preference is being set. */
    private String userUuid;
    /** * The unique identifier of the notification preference being set. */
    private Boolean walletNotifications;
    /** * The unique identifier of the notification preference being set. */
    private Boolean potReminders;
    /** The unique identifier of the notification preference being set. */
    private Boolean scheduleReminders;
    /** The unique identifier of the notification preference being set. */
    private Boolean promotionalNotifications;
    /** * The type of notification preference request
     *  (e.g., CREATE, UPDATE, DELETE). */
    private NotificationPreferenceRequestType preferenceRequestType;
}

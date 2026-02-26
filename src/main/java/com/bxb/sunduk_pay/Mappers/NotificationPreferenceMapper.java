package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.NotificationPreference;
import com.bxb.sunduk_pay.response.NotificationPreferenceResponse;

/** Mapper interface for converting notification preference data to response
 * objects. */
public interface NotificationPreferenceMapper {
    /**
     * Converts a message string to a NotificationPreferenceResponse object.
     *
     * @param preference the message to be included in the response.
     * @return a NotificationPreferenceResponse containing the
     * provided message.
     */
    NotificationPreferenceResponse toNotificationPreferenceResponse(
            NotificationPreference preference);
}

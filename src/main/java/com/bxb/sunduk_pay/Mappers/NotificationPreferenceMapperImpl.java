package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.NotificationPreference;
import com.bxb.sunduk_pay.response.NotificationPreferenceResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/** * Mapper implementation for converting NotificationPreference entities to
 * NotificationPreferenceResponse DTOs.
 */
@Component
@Log4j2
public class NotificationPreferenceMapperImpl
        implements NotificationPreferenceMapper {

    /** Maps a message string to a NotificationPreferenceResponse object. */
    @Override
    public NotificationPreferenceResponse toNotificationPreferenceResponse(
            final NotificationPreference preference) {
        return NotificationPreferenceResponse.builder()
                .walletNotifications(preference.getWalletNotifications())
                .potReminders(preference.getPotReminders())
                .scheduleReminders(preference.getScheduleReminders())
                .promotionalNotifications(preference
                        .getPromotionalNotifications())
                .systemAlerts(preference.getSystemAlerts())
                .build();
    }
}

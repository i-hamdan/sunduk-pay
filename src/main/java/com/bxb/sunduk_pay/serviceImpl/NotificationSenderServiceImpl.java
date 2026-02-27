package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.model.NotificationPreference;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.NotificationPreferenceRepository;
import com.bxb.sunduk_pay.response.NotificationMessage;
import com.bxb.sunduk_pay.service.NotificationSenderService;
import com.bxb.sunduk_pay.service.PushNotificationService;
import com.bxb.sunduk_pay.util.NotificationBuilderUtil;
import com.bxb.sunduk_pay.util.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service responsible for sending notifications to users based on their
 * preferences.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationSenderServiceImpl
        implements NotificationSenderService {

    /** Repository for accessing user notification preferences. */
    private final NotificationPreferenceRepository preferenceRepository;
    /** Utility for building notification messages based on type. */
    private final NotificationBuilderUtil builderUtil;
    /** Service for sending push notifications to users. */
    private final PushNotificationService notificationService;

    /**
     * Sends a notification to the specified user based on the given
     * notification type. The method checks the user's preferences before
     * sending the notification and logs the process.
     *
     * @param user the User to whom the notification will be sent
     * @param type the NotificationType indicating the type of notification to
     *             send
     */
    @Transactional
    public void send(
            final User user,
            final NotificationType type) {

        log.info("Starting notification flow for user UUID: {}",
                user.getUuid());

        if (!isAllowed(user, type)) {
            log.info(
          "Notification blocked due to user preference. UUID: {}",
                    user.getUuid());
            return;
        }


        NotificationMessage message =
                builderUtil.build(type);

        notificationService.sendNotification(
                user.getFcmToken(),
                message.getTitle(),
                message.getBody()
        );

        log.info("Notification sent successfully. UUID: {}",
                user.getUuid());
    }

    /**
     * Checks if the user has allowed notifications of the specified type
     * based on their preferences.
     *
     * @param user the User for whom the notification is being checked
     * @param type the NotificationType to check against the user's preferences
     * @return true if the notification is allowed, false otherwise
     */
    private boolean isAllowed(
            final User user,
            final NotificationType type) {

        if (type == NotificationType.SYSTEM_ALERT) {
            return true;
        }

        NotificationPreference preference =
                preferenceRepository
                        .findByUserUuid(user.getUuid())
                        .orElse(null);

        if (preference == null) {
            log.error("Preference record missing. UUID: {}",
                    user.getUuid());
            return true; // fallback safety
        }

        return switch (type) {
            case WALLET -> preference.getWalletNotifications();
            case POT_REMINDER -> preference.getPotReminders();
            case SCHEDULE_REMINDER ->
                    preference.getScheduleReminders();
            case PROMOTIONAL ->
                    preference.getPromotionalNotifications();
            default ->
                    throw new ResourceNotFoundException(
                            "Unexpected value: " + type);
        };
    }
}
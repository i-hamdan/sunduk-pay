package com.bxb.sunduk_pay.factories.notificationPreferenceFactory;

import com.bxb.sunduk_pay.exception.NotificationPreferenceException;
import com.bxb.sunduk_pay.exception.NotificationPreferenceNotFoundException;
import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.model.NotificationPreference;
import com.bxb.sunduk_pay.repository.NotificationPreferenceRepository;
import com.bxb.sunduk_pay.request.NotificationPreferenceRequest;
import com.bxb.sunduk_pay.response.NotificationPreferenceResponse;
import com.bxb.sunduk_pay.util.NotificationPreferenceRequestType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for updating user notification preferences.
 * This class implements the NotificationPreferenceOperation interface to handle
 * update operations for notification preferences.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class UpdateNotificationPreferenceService
        implements NotificationPreferenceOperation {

    /**
     * Repository for managing notification preferences in the database.
     */
    private final NotificationPreferenceRepository preferenceRepository;

    /**
     * Returns the type of notification preference request that this service
     * handles.
     * In this case, it returns NotificationPreferenceRequestType.
     * UPDATE, indicating that this service is responsible for handling update
     * operations for notification preferences.
     */
    @Override
    public NotificationPreferenceRequestType getPreferenceRequestType() {
        return NotificationPreferenceRequestType.UPDATE;
    }

    /**
     * Performs the update operation for notification preferences based on the
     * provided request.
     * This method is currently a placeholder and returns null. In a complete
     * implementation, it would contain the logic to update the user's
     * notification preferences in the system and return an appropriate response.
     *
     * @param request the NotificationPreferenceRequest containing the details of
     *                the update operation to be performed.
     * @return NotificationPreferenceResponse containing the result of the update
     * operation.
     */
    @Override
    public NotificationPreferenceResponse perform(
            final NotificationPreferenceRequest request) {
        try {
            log.info(
            "Starting update of notification preferences for user UUID: "
                    + request.getUserUuid());
            NotificationPreference notificationPreference = preferenceRepository
                    .findByUserUuid(request.getUserUuid())
              .orElseThrow(() -> new NotificationPreferenceNotFoundException(
                            "User not found with UUID: "
                                    + request.getUserUuid()));

            StringBuilder updatedFields = new StringBuilder();

            if (request.getWalletNotifications() != null) {
                log.info("Updating wallet notifications for user UUID: "
                        + request.getUserUuid() + " to: "
                        + request.getWalletNotifications());
                notificationPreference.setWalletNotifications(
                        request.getWalletNotifications());
                updatedFields.append("walletNotifications, ");
            }
            if (request.getPotReminders() != null) {
                log.info("Updating pot reminders for user UUID: "
                        + request.getUserUuid() + " to: "
                        + request.getPotReminders());
                notificationPreference.setPotReminders(
                        request.getPotReminders());
                updatedFields.append("potReminders, ");
            }
            if (request.getScheduleReminders() != null) {
                log.info("Updating schedule reminders for user UUID: "
                        + request.getUserUuid() + " to: "
                        + request.getScheduleReminders());
                        notificationPreference.setScheduleReminders(
                        request.getScheduleReminders());
                updatedFields.append("scheduleReminders, ");
            }
            if (request.getPromotionalNotifications() != null) {
                log.info("Updating promotional notifications for user UUID: "
                        + request.getUserUuid() + " to: "
                        + request.getPromotionalNotifications());
                notificationPreference.setPromotionalNotifications(
                        request.getPromotionalNotifications());
                updatedFields.append("promotionalNotifications, ");
            }

            if (updatedFields.isEmpty()) {
                log.error(
         "No valid preference fields provided for update for user UUID: "
                                + request.getUserUuid());
                throw new ResourceNotFoundException(
                        "No valid preference fields provided for update.");
            }

            // Remove the trailing comma and space from the updated fields string
            String fields = updatedFields.substring(0,
                    updatedFields.length() - 2);

            preferenceRepository.save(notificationPreference);
            return NotificationPreferenceResponse.builder()
                    .message("Notification preferences updated successfully!")
                    .updatedFields(List.of(fields))
                    .build();
        } catch (Exception e) {
            log.error(
      "Error updating notification preferences for user UUID: "
                    + request.getUserUuid(), e);
            throw new NotificationPreferenceException(
                    "Failed to update notification preferences: "
                            + e.getMessage());
        }
    }
}
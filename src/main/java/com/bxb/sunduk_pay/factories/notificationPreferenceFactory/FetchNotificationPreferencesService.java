package com.bxb.sunduk_pay.factories.notificationPreferenceFactory;

import com.bxb.sunduk_pay.Mappers.NotificationPreferenceMapper;
import com.bxb.sunduk_pay.exception.NotificationPreferenceException;
import com.bxb.sunduk_pay.exception.NotificationPreferenceNotFoundException;
import com.bxb.sunduk_pay.model.NotificationPreference;
import com.bxb.sunduk_pay.repository.NotificationPreferenceRepository;
import com.bxb.sunduk_pay.request.NotificationPreferenceRequest;
import com.bxb.sunduk_pay.response.NotificationPreferenceResponse;
import com.bxb.sunduk_pay.util.NotificationPreferenceRequestType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service implementation for fetching user notification preferences.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class FetchNotificationPreferencesService
        implements NotificationPreferenceOperation {

    /**
     * Repository for accessing notification preference data.
     */
    private final NotificationPreferenceRepository preferenceRepository;

    /**
     * Mapper for converting between NotificationPreference entities and
     * response objects.
     */
    private final NotificationPreferenceMapper notificationPreferenceMapper;

    /**
     * Retrieves the type of request this service handles.
     */
    @Override
    public NotificationPreferenceRequestType getPreferenceRequestType() {
        return NotificationPreferenceRequestType.FETCH;
    }

    /**
     * Performs the operation to fetch user notification preferences based on
     * the provided request.
     */
    @Override
    public NotificationPreferenceResponse perform(
            final NotificationPreferenceRequest request) {
        try {
          NotificationPreference notificationPreference = preferenceRepository.
                    findByUserUuid(request.getUserUuid())
               .orElseThrow(() -> new NotificationPreferenceNotFoundException(
                      "Notification preferences not found for user with UUID: "
                                    + request.getUserUuid()));
            log.info(
      "Fetched notification preferences for user with UUID: {}",
                    request.getUserUuid());

            // Map the NotificationPreference entity to a response object
          NotificationPreferenceResponse response = notificationPreferenceMapper
                    .toNotificationPreferenceResponse(
                            notificationPreference);
            // Set a success message in the response
            response.setMessage(
                    "Notification preferences fetched successfully.");
            return response;
        } catch (Exception e){
            log.error(
        "Error fetching notification preferences for user UUID: {}. ",
                    request.getUserUuid()
            );
            throw new NotificationPreferenceException(
                "An error occurred while fetching notification preferences: "
                            + e.getMessage());
        }
    }
}

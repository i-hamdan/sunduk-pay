package com.bxb.sunduk_pay.serviceImpl;


import com.bxb.sunduk_pay.exception.UserActivityLogException;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.logModel.UserActivityLog;
import com.bxb.sunduk_pay.repository.UserActivityLogRepository;
import com.bxb.sunduk_pay.service.ActivityLogService;
import com.bxb.sunduk_pay.util.ActivityLogMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of {@link ActivityLogService} for processing.
 * saving user activity logs. Consumes events and stores them in
 * the database.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {
    /** Utility for building activity log messages. */
    private final ActivityLogMessageUtil activityLogMessageUtil;
    /** Repository for persisting user activity logs. */
    private final UserActivityLogRepository activityLogRepository;

    /**
     * Processes a user activity event.
     * And saves the corresponding log entry in the database.
     * Builds a descriptive message based on the event type.
     * @param event the user Kafka event containing activity details
     * @throws UserActivityLogException if saving the log entry fails
     */
    @Override
    public void processUserActivity(final UserKafkaEvent event) {
        try {
            String description = activityLogMessageUtil.buildDescription(event);

            UserActivityLog activityLog = UserActivityLog.builder()
                    .logId(UUID.randomUUID().toString())
                    .uuid(event.getUuid())
                    .email(event.getEmail())
                    .fullName(event.getFullName())
                    .action(event.getEventType())
                    .description(description)
                    .localDateTime(LocalDateTime.now())
                    .build();

            activityLogRepository.save(activityLog);
            log.info("Saved user activity log: {}", activityLog);

        } catch (Exception e) {
            log.error("Error saving user activity log for email: {}",
                    event.getEmail(), e);
            throw new UserActivityLogException(
                    "Failed to process user activity log for: "
                    + event.getEmail());
        }
    }

}

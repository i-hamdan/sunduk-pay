package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.exceptions.UserActivityLogException;
import com.bxb.sunduk_pay.logModel.UserActivityLog;
import com.bxb.sunduk_pay.repository.UserActivityLogRepository;
import com.bxb.sunduk_pay.service.ActivityLogService;
import com.bxb.sunduk_pay.util.ActivityLogMessageUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementation of {@link ActivityLogService} for processing and
 * saving user activity logs. Consumes events and stores them in
 * the database.
 */
@Service
@Log4j2
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogMessageUtil activityLogMessageUtil;
    private final UserActivityLogRepository activityLogRepository;

    /**
     * Constructs an {@code ActivityLogServiceImpl} with required dependencies.
     *
     * @param activityLogMessageUtil utility for building log descriptions
     * @param activityLogRepository repository for storing user activity logs
     */
    public ActivityLogServiceImpl(ActivityLogMessageUtil activityLogMessageUtil,
                                  UserActivityLogRepository activityLogRepository) {
        this.activityLogMessageUtil = activityLogMessageUtil;
        this.activityLogRepository = activityLogRepository;
    }


    public void processUserActivity(UserKafkaEvent event) {
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
            log.error("Error saving user activity log for email: {}", event.getEmail(), e);
            throw new UserActivityLogException("Failed to process user activity log for: " + event.getEmail());
        }
    }
}

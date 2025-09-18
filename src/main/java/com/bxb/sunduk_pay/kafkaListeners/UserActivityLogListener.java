package com.bxb.sunduk_pay.kafkaListeners;

import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.service.ActivityLogService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener that consumes user events from "user-topic"
 * and sends them to {@link ActivityLogService} for logging.
 */
@Component
public  class UserActivityLogListener {  // Made class final (DesignForExtension fix)

    /** Service used for logging user activities. */
    private final ActivityLogService activityLogService;

    /**
     * Constructs a new {@code UserActivityLogListener}.
     *
     * @param activityLogService the service that handles logging user activities
     */
    public UserActivityLogListener(final ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    /**
     * Consumes a {@link UserKafkaEvent} from Kafka and delegates it
     * to the {@link ActivityLogService} for logging.
     *
     * @param userKafkaEvent the user event (never {@code null})
     */
    @KafkaListener(
            topics = "user-topic",
            groupId = "activity-log-group",
            concurrency = "3"
    )
    public void consumeActivityLog(final UserKafkaEvent userKafkaEvent) {
        // Delegate the processing to ActivityLogService
        activityLogService.processUserActivity(userKafkaEvent);
    }
}

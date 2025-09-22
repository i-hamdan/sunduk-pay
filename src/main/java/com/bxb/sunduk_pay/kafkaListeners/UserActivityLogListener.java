package com.bxb.sunduk_pay.kafkaListeners;

import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.service.ActivityLogService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka listener for user activity logs.
 * Listens to the "user-topic" Kafka topic and processes incoming user activity events.
 */
@Component
public class UserActivityLogListener {
    private final ActivityLogService activityLogService;

    public UserActivityLogListener(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    /**
     * Consumes user activity log events from the Kafka topic and processes them.
     *
     * @param userKafkaEvent the user activity event received from Kafka
     */
    @KafkaListener(topics = "user-topic",
            groupId = "activity-log-group",
            concurrency = "3")
    public void consumeActivityLog(UserKafkaEvent userKafkaEvent) {
        activityLogService.processUserActivity(userKafkaEvent);
    }
}

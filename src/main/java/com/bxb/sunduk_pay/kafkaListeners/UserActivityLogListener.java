package com.bxb.sunduk_pay.kafkaListeners;

import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.service.ActivityLogService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserActivityLogListener {
    private final ActivityLogService activityLogService;

    public UserActivityLogListener(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }
    @KafkaListener(topics = "user-topic", groupId = "activity-log-group", concurrency = "3")
    public void consumeActivityLog(UserKafkaEvent userKafkaEvent) {
        activityLogService.processUserActivity(userKafkaEvent);
    }
}

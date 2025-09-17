package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;

public interface ActivityLogService {
    void processUserActivity(UserKafkaEvent event);
}

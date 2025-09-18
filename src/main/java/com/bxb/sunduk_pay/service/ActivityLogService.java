package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;

/**
 * Service interface for handling user activity logging.
 */
public interface ActivityLogService {

    /**
     * Processes user activity events.
     *
     * @param event the user activity event to process
     */
    void processUserActivity(UserKafkaEvent event);
}

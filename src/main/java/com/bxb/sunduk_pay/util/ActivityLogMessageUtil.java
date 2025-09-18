package com.bxb.sunduk_pay.util;

import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import org.springframework.stereotype.Component;

@Component
public class ActivityLogMessageUtil {

    /**
     * Builds a user-friendly description for a {@link UserKafkaEvent}.
     *
     * @param event the user event received from Kafka
     * @return a descriptive message for logging or notifications
     */
    public String buildDescription(UserKafkaEvent event) {
        if ("LOGIN".equalsIgnoreCase(event.getEventType())) {
            return "User '" + event.getFullName() + "' successfully logged in to the system.";
        } else if ("SIGNUP".equalsIgnoreCase(event.getEventType())) {
            return "New user registration completed for '" + event.getFullName() + "'.";
        } else {
            return "User event received for '" + event.getFullName() + "' with unknown action.";
        }
    }
}



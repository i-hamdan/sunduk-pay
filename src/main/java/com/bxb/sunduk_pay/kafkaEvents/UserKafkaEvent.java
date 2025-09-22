package com.bxb.sunduk_pay.kafkaEvents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user-related event to be sent via Kafka.
 * This event can be used for logging user activities such as login and signup.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserKafkaEvent {
    private String uuid;
    private String fullName;
    private String email;
    private String eventType; // "LOGIN" or "SIGNUP"
}

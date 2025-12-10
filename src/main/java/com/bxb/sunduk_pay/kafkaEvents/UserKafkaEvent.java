package com.bxb.sunduk_pay.kafkaEvents;

import com.bxb.sunduk_pay.util.EmailCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user-related event to be sent via Kafka.
 * This event can be used for logging user activities
 *such as login and signup.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserKafkaEvent {
    /**
     * Unique identifier for the user.
     */
    private String uuid;

    /**
     * Full name of the user.
     */
    private String fullName;

    /**
     * Email address of the user.
     */
    private String email;

    /**
     * Type of event, e.g., "LOGIN" or "SIGNUP".
     */
    private String eventType;

    /**
     * Category of the email to be sent.
     */
    private EmailCategory emailCategory;
}

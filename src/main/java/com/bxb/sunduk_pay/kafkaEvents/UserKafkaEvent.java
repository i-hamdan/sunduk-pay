package com.bxb.sunduk_pay.kafkaEvents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Kafka event carrying user-related actions such as login or signup.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserKafkaEvent {

    /** Unique user identifier. */
    private String uuid;

    /** Full name of the user. */
    private String fullName;

    /** Email of the user. */
    private String email;

    /** Event type performed by the user (e.g., LOGIN, SIGNUP). */
    private String eventType;
}

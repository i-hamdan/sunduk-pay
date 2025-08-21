package com.bxb.sunduk_pay.kafkaEvents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserKafkaEvent {
    private String uuid;
    private String fullName;
    private String email;
    private String eventType; // "LOGIN" or "SIGNUP"
}

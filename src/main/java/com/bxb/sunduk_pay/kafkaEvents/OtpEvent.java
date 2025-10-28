package com.bxb.sunduk_pay.kafkaEvents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OtpEvent {
    /**
     * The ID of the user who achieved the milestone.
     */
    private String userId;

    /**
     * The full name of the user who achieved the milestone.
     */
    private String fullname;


    private String otp;
    /**
     * The email of the user who achieved the milestone.
     */
    private String email;

}
/// mapper banan jo user se event convert kredega  ??
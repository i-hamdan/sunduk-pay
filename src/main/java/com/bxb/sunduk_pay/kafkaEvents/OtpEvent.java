package com.bxb.sunduk_pay.kafkaEvents;

import com.bxb.sunduk_pay.util.EmailCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event class representing an OTP (One-Time Password) event.
 */
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
/**
     * The OTP code generated for the user.
     */

    private String otp;
    /**
     * The email of the user who achieved the milestone.
     */
    private String email;

    /**
     * The category of the email to be sent.
     */
    private EmailCategory emailCategory;
}

package com.bxb.sunduk_pay.kafkaEvents;

import com.bxb.sunduk_pay.util.EmailCategory;
import com.bxb.sunduk_pay.util.OtpPurpose;
import lombok.*;

/**
 * Event class representing an OTP (One-Time Password) event.
 */
@Getter
@Setter
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
     * The email of the user who will receive the OTP.
     */
    private String email;
    /**
     * The phone number of the user who will receive the OTP.
     */
    private String phoneNumber;
    /**
     * The category of the email to be sent.
     */
    private EmailCategory emailCategory;

    /**
     * The purpose of the OTP.
     */
    private OtpPurpose otpPurpose;
}

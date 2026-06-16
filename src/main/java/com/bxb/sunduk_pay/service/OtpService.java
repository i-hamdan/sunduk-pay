package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;

/**
 * Service interface for handling OTP-related operations.
 */
public interface OtpService {
    /**
     * Sends an OTP to the specified identifier via the given channel.
     *
     * @param identifier the identifier (e.g., email or phone number) \to
     *                   send the OTP to
     * @param channel    the channel through which to send the OTP
     *                   (e.g., "email", "sms")
     */
    void sendOtp(String identifier, String channel);
    /**
     * Generates a new OTP.
     *
     * @return the generated OTP as a String
     */
    String generateOtp();

    /**
     * Verifies the provided OTP for the given phone number.
     *
     * @param request UserRequest containing phone number and OTP to verify
     * @return UserResponse indicating the result of the verification
     */
    UserResponse verifyOtp(UserRequest request);
}

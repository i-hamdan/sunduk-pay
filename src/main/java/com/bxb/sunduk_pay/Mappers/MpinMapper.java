package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;

/**
 * Mapper interface for converting data
 * to OtpEvent objects.
 */
public interface MpinMapper {
    /**
     * Converts provided data to an OtpEvent.
     *
     * @param uuid     the unique identifier
     * @param email    the email address
     * @param fullName the full name of the user
     * @param otp      the one-time password
     * @return the corresponding OtpEvent
     */
OtpEvent toOtpEvent(String uuid, String email, String fullName, String otp);
}

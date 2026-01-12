package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;

/**
 * Mapper interface for converting data
 * to OtpEvent objects.
 */
public interface MpinMapper {
OtpEvent toOtpEvent(String uuid, String email, String fullname,
                    String otp);
}

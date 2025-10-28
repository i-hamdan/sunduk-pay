package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;

public interface MpinMapper{
OtpEvent toOtpEvent(String uuid, String email, String fullname,
                    String otp);
}

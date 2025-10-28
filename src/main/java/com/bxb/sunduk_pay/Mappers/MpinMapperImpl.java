package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;
import org.springframework.stereotype.Component;

@Component
public class MpinMapperImpl implements MpinMapper {


    @Override
    public OtpEvent toOtpEvent(String uuid, String email,
                               String fullname,
                               String otp) {
        return OtpEvent.builder()
                .userId(uuid)
                .fullname(fullname)
                .email(email)
                .otp(otp)
                .build();
    }
}

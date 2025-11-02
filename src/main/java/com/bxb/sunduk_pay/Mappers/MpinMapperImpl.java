package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;
import org.springframework.stereotype.Component;

@Component
public class MpinMapperImpl implements MpinMapper {


    @Override
    public OtpEvent toOtpEvent(String uuid, String email,
                               String fullname,
                               String otp) {

        String firstname = fullname.trim().split("\\s+")[0];
        return OtpEvent.builder()
                .userId(uuid)
                .fullname(firstname)
                .email(email)
                .otp(otp)
                .build();
    }
}

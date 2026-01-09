package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;
import org.springframework.stereotype.Component;
/**
 * Mapper implementation for converting data
 * to OtpEvent objects.
 */
@Component
public class MpinMapperImpl implements MpinMapper {

   /**
     * Converts user details to an OtpEvent.
     *
     * @param uuid     the user's unique identifier
     * @param email    the user's email address
     * @param fullname the user's full name
     * @param otp      the one-time password
     * @return an OtpEvent object containing the provided details
     */
    @Override
    public OtpEvent toOtpEvent(final String uuid, final String email,
                               final String fullname,
                               final String otp) {

        String firstname = fullname.trim().split("\\s+")[0];
        return OtpEvent.builder()
                .userId(uuid)
                .fullname(firstname)
                .email(email)
                .otp(otp)
                .build();
    }
}

package com.bxb.sunduk_pay.factories.mpinFactory;

import com.bxb.sunduk_pay.Mappers.MpinMapper;
import com.bxb.sunduk_pay.encryption.MpinValidations;
import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.MpinRequest;
import com.bxb.sunduk_pay.response.MpinResponse;
import com.bxb.sunduk_pay.util.MpinRequestType;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Service class responsible for generating and sending OTPs
 * as part of the MPIN operations.
 */
@Service
@RequiredArgsConstructor
public class GenerateOtp implements MpinOperation {

    /** Upper bound for OTP generation. */
    private static final int BOUNDS = 10000;

    /**
     * Mapper for MPIN-related data transformations.
     */
    private final MpinMapper mpinMapper;
    /**
     * Kafka template for sending forgot MPIN events.
     */
    private final KafkaTemplate<String, OtpEvent> kafkaTemplate;
    /**
    validation utility.
     */
    private final MpinValidations validations;
    /**
     * Cache to store OTPs temporarily.
     */
     private final Cache<String, String> otpCache;

    /**
     * Returns the type of MPIN request this operation handles.
     * @return MpinRequestType associated with this operation.
     */
    @Override
    public MpinRequestType getMpinRequestType() {
        return MpinRequestType.OTP;
    }
/**
     * Performs the OTP generation and sending
 * operation based on the provided request.
 * @param mpinRequest the request containing necessary data for the operation.
 * @return MpinResponse containing the result of the operation.
     */
    @Override
    public MpinResponse perform(final MpinRequest mpinRequest) {
        // validate user email
        User user = validations.
                getUserEmailInfo(mpinRequest.getEmail());
       // generating otp
        String otp = String
                .format("%04d", new Random()
                        .nextInt(BOUNDS));
          // saved otp in chache for 5 minutes
            otpCache.put(user.getEmail(), otp);
            // create otp event
        OtpEvent otpEvent = mpinMapper.toOtpEvent(user.getUuid(),
                user.getEmail(), user.getFullName(), otp);
         // send otp event to kafka topic
        kafkaTemplate.send("otp-topic", otpEvent);

        return MpinResponse.builder()
                .message("A Verification code  has been sent to your "
                        + "registered "
                        + "email.")
                .build();
    }
}

package com.bxb.sunduk_pay.factories.userFactory;

import com.bxb.sunduk_pay.encryption.HashUtil;
import com.bxb.sunduk_pay.encryption.UserInfoEncryption;
import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.util.OtpChannel;
import com.bxb.sunduk_pay.util.OtpPurpose;
import com.bxb.sunduk_pay.util.UserRequestType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Log4j2
public class SendOtp implements UserOperation {

    /**
     * Time-to-live for OTPs in minutes.
     */
    private static final int OTP_TTL = 5;
    /**
     * Maximum number of allowed OTP verification
     * attempts before invalidating the OTP.
     */
    private static final int MAX_ATTEMPTS = 5;
    /**
     * KafkaTemplate for sending OTP events to Kafka topics.
     */
    private final KafkaTemplate<String, OtpEvent> kafkaTemplate;
    /**
     * RedisTemplate for storing OTP data and rate-limiting information in
     * Redis.
     */
    private final RedisTemplate<String, Object> redis;
    /**
     * Repository for user-related database operations.
     */
    private final UserRepository userRepository;
    /**
     * Encryption utility for handling user information encryption
     * and decryption.
     */
    private final UserInfoEncryption userInfoEncryption;
    /**
     * Hash utility for hashing operations, such as hashing
     * phone numbers or emails.
     */
    private final HashUtil hashUtil;

    @Override
    public UserRequestType getUserRequestType() {
        return UserRequestType.SEND_OTP;
    }

    @Override
    public UserResponse perform(UserRequest userRequest) {

        String identifier = userRequest.getIdentifier();
        String channel = userRequest.getChannel();

        //1. Rate limiting (1 OTP / min per identifier)
        String rateKey = "otp:rate:" + channel.toLowerCase() + ":" + identifier;
        if (Boolean.TRUE.equals(redis.hasKey(rateKey))) {
            throw new RuntimeException("OTP requested too frequently");
        }

        // 2. Generate OTP
        String otp =
                String.valueOf(100000 + new SecureRandom()
                        .nextInt(900000));

        log.info("Generated OTP: {} for identifier: {}", otp, identifier);
        String otpHash = BCrypt.hashpw(otp, BCrypt.gensalt());

        Map<String, String> payload = Map.of("otpHash", otpHash, "createdAt",
                LocalDateTime.now().toString(), "channel", channel);

        // 3. Store OTP
        String otpKey = "otp:" + channel.toLowerCase() + ":" + identifier;

        redis.opsForValue().set(otpKey, payload, OTP_TTL, TimeUnit.MINUTES);

        // 4. Apply rate limit
        redis.opsForValue().set(rateKey, "1", 60,
                TimeUnit.SECONDS);
        log.info("Stored OTP in Redis for identifier: {}", identifier);

        if (channel.equalsIgnoreCase(OtpChannel.SMS.name())) {
            OtpEvent otpEvent =
                    OtpEvent.builder()
                            .otp(otp)
                            .otpPurpose(OtpPurpose.SIGNUP)
                            .phoneNumber(identifier).build();
            kafkaTemplate.send("otp-sms-topic", otpEvent);
        }

        if (channel.equalsIgnoreCase(OtpChannel.EMAIL.name())) {
            OtpEvent otpEvent =
                    OtpEvent.builder().otp(otp).otpPurpose(
                            OtpPurpose.SIGNUP).email(identifier).build();
            kafkaTemplate.send("otp-email-topic", otpEvent);
        }


        log.info("OTP event sent to Kafka for identifier: {}",
                identifier);

        return UserResponse.builder().message("OTP sent successfully").build();
    }

}

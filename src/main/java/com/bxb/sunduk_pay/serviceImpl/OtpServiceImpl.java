package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.encryption.HashUtil;
import com.bxb.sunduk_pay.encryption.UserInfoEncryption;
import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;
import com.bxb.sunduk_pay.model.AuthProvider;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.service.OtpService;
import com.bxb.sunduk_pay.util.OtpChannel;
import com.bxb.sunduk_pay.util.OtpPurpose;
import com.bxb.sunduk_pay.util.AuthProviderMethod;
import com.bxb.sunduk_pay.util.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Service implementation for handling OTP (One-Time Password) operations.
 * Responsible for generating, sending, and storing OTPs via different channels.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

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
     * RedisTemplate for storing OTP data and rate-limiting information in Redis.
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

    /** {@inheritDoc} */

    @Override
    public void sendOtp(String identifier, String channel) {


        //1. Rate limiting (1 OTP / min per identifier)
        String rateKey = "otp:rate:" + channel.toLowerCase() + ":" + identifier;
        if (Boolean.TRUE.equals(redis.hasKey(rateKey))) {
            throw new RuntimeException("OTP requested too frequently");
        }

        // 2. Generate OTP
        String otp = generateOtp();
        log.info("Generated OTP: {} for identifier: {}", otp, identifier);
        String otpHash = BCrypt.hashpw(otp, BCrypt.gensalt());

        Map<String, String> payload = Map.of("otpHash", otpHash, "createdAt",
                LocalDateTime.now().toString(), "channel", channel);

        // 3. Store OTP
        String otpKey = "otp:" + channel.toLowerCase() + ":" + identifier;

        redis.opsForValue().set(otpKey, payload, OTP_TTL, TimeUnit.MINUTES);

        // 4. Apply rate limit
        redis.opsForValue().set(rateKey, "1", 60, TimeUnit.SECONDS);
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
                    OtpEvent.builder()
                            .otp(otp)
                            .otpPurpose(OtpPurpose.SIGNUP)
                            .email(identifier).build();
            kafkaTemplate.send("otp-email-topic", otpEvent);
        }


        log.info("OTP event sent to Kafka for identifier: {}", identifier);
    }

    /** {@inheritDoc} */

    @Override
    public String generateOtp() {
        return String.valueOf(100000 + new SecureRandom().nextInt(900000));
    }

    /** {@inheritDoc} */

    @Override
    public UserResponse verifyOtp(UserRequest request) {

        String otpKey =
                "otp:" + request.getChannel().toString()
                        .toLowerCase() + ":" + request.getIdentifier();
        String attemptKey =
                "otp:attempts:" + request.getChannel().toString()
                        .toLowerCase() + ":" + request.getIdentifier();

        Map<String, String> payload =
                (Map<String, String>) redis.opsForValue().get(otpKey);

        if (payload == null) {
            throw new RuntimeException("OTP expired or not found");
        }

        int attempts =
                Optional.ofNullable(
                        redis.opsForValue().get(attemptKey))
                        .map(v -> Integer.parseInt(
                                v.toString())).orElse(0);

        if (attempts >= MAX_ATTEMPTS) {
            redis.delete(otpKey);
            throw new RuntimeException("Too many attempts");
        }

        if (!BCrypt.checkpw(request.getOtp(), payload.get("otpHash"))) {
            redis.opsForValue().increment(attemptKey);
            redis.expire(attemptKey, 5, TimeUnit.MINUTES);
            throw new RuntimeException("Invalid OTP");
        }
        if (OtpChannel.SMS.equals(request.getChannel()) &&
                request.getIsSignupFlow().equals("signupFlag")){
            AuthProvider authProvider = AuthProvider.builder()
                    .authProviderMethod(AuthProviderMethod.LOCAL_PHONE)
                    .build();
            Set<AuthProvider> authProviders = Set.of(authProvider);
            User user = User.builder()
                    .authProviders(authProviders)
                    .phoneNumberHash(hashUtil.sha256(request.getIdentifier()))
                    .userStatus(UserStatus.INACTIVE)
                    .build();
            authProvider.setUser(user);
            userRepository.save(user);
            log.info("Created inactive user with phone number: {}",
                    request.getIdentifier());
        }
        else if (OtpChannel.EMAIL.equals(request.getChannel())
                && request.getIsSignupFlow().equals("signupFlag")){

            AuthProvider authProvider = AuthProvider.builder()
                    .authProviderMethod(AuthProviderMethod.LOCAL_EMAIL)
                    .build();
            Set<AuthProvider> authProviders = Set.of(authProvider);
            User user = User.builder()
                    .email(request.getIdentifier())
                    .userStatus(UserStatus.INACTIVE)
                    .authProviders(authProviders)
                    .build();
            authProvider.setUser(user);
            userRepository.save(user);
            log.info("Created inactive user with email: {}",
                    request.getIdentifier());
        }

        // Success
        redis.delete(otpKey);
        redis.delete(attemptKey);


        return UserResponse.builder()
                .message("OTP verified successfully")
                .build();
    }
}


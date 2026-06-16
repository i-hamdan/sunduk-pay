package com.bxb.sunduk_pay.factories.userFactory;

import com.bxb.sunduk_pay.encryption.HashUtil;
import com.bxb.sunduk_pay.exception.InvalidOtpException;
import com.bxb.sunduk_pay.exception.OtpAttemptsExceededException;
import com.bxb.sunduk_pay.exception.OtpExpiredException;
import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;
import com.bxb.sunduk_pay.model.AuthProvider;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.util.AuthProviderMethod;
import com.bxb.sunduk_pay.util.OtpChannel;
import com.bxb.sunduk_pay.util.UserRequestType;
import com.bxb.sunduk_pay.util.UserStatus;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.catalina.authenticator.SavedRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Log4j2
public class VerifyOtp implements UserOperation {

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
     * Hash utility for hashing operations, such as hashing
     * phone numbers or emails.
     */
    private final HashUtil hashUtil;

    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;

    @Override
    public UserRequestType getUserRequestType() {
        return UserRequestType.VERIFY_OTP;
    }

    @Override
    public UserResponse perform(UserRequest request) {

            String otpKey =
                    "otp:" + request.getChannel().toLowerCase() +
                            ":" + request.getIdentifier();
            String attemptKey =
                    "otp:attempts:" + request.getChannel().toLowerCase() +
                            ":" + request.getIdentifier();

            Map<String, String> payload =
                    (Map<String, String>) redis.opsForValue().get(otpKey);

            if (payload == null) {
                throw new OtpExpiredException("OTP expired or not found");
            }

            int attempts =
                    Optional.ofNullable(redis.opsForValue().get(attemptKey))
                            .map(v -> Integer.parseInt(
                                    v.toString())).orElse(0);

            if (attempts >= MAX_ATTEMPTS) {
                redis.delete(otpKey);
                throw new OtpAttemptsExceededException("Too many attempts " +
                        "request a new OTP");
            }

            if (!BCrypt.checkpw(request.getOtp(), payload.get("otpHash"))) {
                redis.opsForValue().increment(attemptKey);
                redis.expire(attemptKey, 1, TimeUnit.MINUTES);
                throw new InvalidOtpException("Please enter correct OTP");
            }
            if (OtpChannel.SMS.toString().equals(request.getChannel())
                    && request.getIsSignupFlow().equals("signupFlag")) {
                AuthProvider authProvider =
                        AuthProvider.builder().authProviderMethod(
                                AuthProviderMethod.LOCAL_PHONE).build();
                Set<AuthProvider> authProviders = Set.of(authProvider);
                User user =
                        User.builder()
                                .authProviders(authProviders)
                                .isDeleted(false)
                                .phoneNumberHash(hashUtil.sha256(
                                        request.getIdentifier()))
                                .userStatus(UserStatus.INACTIVE).build();
                authProvider.setUser(user);
                userRepository.save(user);
                log.info("Created inactive user with phone number: {}",
                        request.getIdentifier());

                Cookie uuidCookie = new Cookie("user_uuid",
                        user.getUuid());
                uuidCookie.setSecure(true);

                httpServletRequest.getSession(true);
                httpServletResponse.addCookie(uuidCookie);

            } else if (OtpChannel.EMAIL.toString().equals(request.getChannel())
                    && request.getIsSignupFlow().equals("signupFlag")) {

                AuthProvider authProvider =
                        AuthProvider.builder()
                                .authProviderMethod(AuthProviderMethod
                                        .LOCAL_EMAIL)
                                .build();
                Set<AuthProvider> authProviders = Set.of(authProvider);
                User user =
                        User.builder().email(request.getIdentifier())
                                .userStatus(UserStatus.INACTIVE)
                                .isDeleted(false)
                                .authProviders(authProviders).build();
                authProvider.setUser(user);
                userRepository.save(user);
                log.info("Created inactive user with email: {}",
                        request.getIdentifier());

                Cookie uuidCookie = new Cookie("user_uuid",
                        user.getUuid());
                uuidCookie.setPath("/");
                uuidCookie.setSecure(true);
                uuidCookie.setHttpOnly(false);


                httpServletRequest.getSession(true);
                httpServletResponse.addCookie(uuidCookie);
            }

            // Success
            redis.delete(otpKey);
            redis.delete(attemptKey);

        return UserResponse.builder()
                    .message("OTP verified successfully")
                    .build();
    }
}

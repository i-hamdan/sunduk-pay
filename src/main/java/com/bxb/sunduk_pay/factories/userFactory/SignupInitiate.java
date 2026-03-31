package com.bxb.sunduk_pay.factories.userFactory;

import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.util.UserRequestType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class SignupInitiate implements UserOperation {

    private final SendOtp otpService;

    @Override
    public UserRequestType getUserRequestType() {
        return UserRequestType.SIGNUP_INITIATE;
    }

    @Override
    public UserResponse perform(UserRequest request) {

        if (request.getPhoneNumber() != null && !request.getPhoneNumber()
                .isBlank()) {
            otpService.perform(request);
            log.info("Proceeding to send OTP via Phone Number to: {}",
                    request.getPhoneNumber());
        } else {
            // Fallback to Email
            otpService.perform(request);
            log.info("Proceeding to send OTP via Email to: {}",
                    request.getEmail());
        }
        return UserResponse.builder().message("OTP sent successfully to " +
                "initiate signup").build();
    }
}


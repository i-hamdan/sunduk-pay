package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.OtpChannel;
import com.bxb.sunduk_pay.util.UserRequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    /**
     * Unique identifier for the user.
     */
    private String uuid;
    /**
     * Email address of the user.
     */
    private String email;
    /**
     * Full name of the user.
     */
    private String fullName;
    /**
     * Date of birth of the user.
     */
    private LocalDate dateOfBirth;
    /**
     * Phone number of the user.
     */
    private String phoneNumber;
    /**
     * Mobile PIN for user authentication.
     */
    private String mpin;
    /**
     * Address of the user.
     */
    private String address;
    /**
     * city of User.
     */
    private String city;
    /**
     * Country of User.
     */
    private String country;
    /**
     * Type of user request (e.g., CREATE, UPDATE).
     */
    private UserRequestType userRequestType;
    /**
     * OTP for verification during authentication or signup.
     */
    private String otp;
    /**
     * Identifier for OTP verification (e.g., email or phone number).
     */
    private String identifier;
    /**
     * Channel through which the OTP was sent (e.g., EMAIL, SMS).
     */
    private String channel;
    /**
     * Password for user authentication (if applicable).
     */
    private String password;
    /**
     *  Indicates if the OTP verification is part of the signup flow.
     */
    private String isSignupFlow;
    /**
     * Old password for password change operations.
     */
    private String oldPassword;
    /**
     * New password for password change operations.
     */
    private String newPassword;
    /**
     * Landing page preference for the user.
     */
    private String landingPage;

}

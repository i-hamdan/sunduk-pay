package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.UserRequestType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
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
     * Present address of the user.
     */
    private String presentAddress;
    /**
     * Permanent address of the user.
     */
    private String permanentAddress;
    /**
     * Type of user request (e.g., CREATE, UPDATE).
     */
    private UserRequestType userRequestType;

    /**
     * Landing page URL for the user.
     */
    private String landingPage;
}

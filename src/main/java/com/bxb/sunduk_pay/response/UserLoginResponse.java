package com.bxb.sunduk_pay.response;

import lombok.Data;

/**
 * Response object for user login containing user details.
 */
@Data
public class UserLoginResponse {
    /** The unique identifier of the user. */
    private String uuid;
    /** The email address of the user. */
    private String email;
    /** The full name of the user. */
    private String fullName;
    /** The phone number of the user. */
    private String phoneNumber;
}















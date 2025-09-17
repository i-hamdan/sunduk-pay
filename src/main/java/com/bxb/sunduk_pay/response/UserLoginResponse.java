package com.bxb.sunduk_pay.response;

import lombok.Data;

/**
 * Response payload for user login operations.
 * <p>
 * Contains basic user information returned after successful authentication.
 * </p>
 */
@Data
public class UserLoginResponse {

    /** Unique identifier of the user. */
    private String uuid;

    /** User's email address. */
    private String email;

    /** User's full name. */
    private String fullName;

    /** User's phone number. */
    private String phoneNumber;
}

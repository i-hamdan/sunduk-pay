package com.bxb.sunduk_pay.util;

/**
 * Enum representing different types of user requests.
 */
public enum UserRequestType {
    /**
     * Represents a request to update user information.
     */
    UPDATE,
    /**
     * Represents a request to retrieve user details.
     */
    USER_DETAILS,
    /**
     * Represents a request to verify the user's MPIN.
     */
    VERIFY_MPIN,
    /**
     * fetches the profile photo of the user.
     */
    FETCH_PROFILE_PHOTO,
    /**
     * Represents a request to change the user's landing page.
     */
    CHANGE_LANDING_PAGE,
    /**
     * Represents a request to change the user's password.
     */
    CHANGE_PASSWORD,
    /**
     * Represents a request to send an OTP for verification.
     */
    SEND_OTP,
    /**
     * Represents a request to verify the OTP provided by the user.
     */
    VERIFY_OTP,
    /**
     * Represents a request to log in the user.
     */
    LOGIN,
    /**
     * Represents a request to check if a user already exists
     * (e.g., during signup).
     */
    CHECK_IF_EXISTS,
        /**
        * Represents a request to initiate the signup process for a new user.
        */
    SIGNUP_INITIATE,
    /**
     * Represents a request to complete the signup process for a new user.
     */
    SIGNUP_COMPLETE;
}

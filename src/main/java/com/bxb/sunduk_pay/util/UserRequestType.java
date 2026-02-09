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
    CHANGE_LANDING_PAGE;
}

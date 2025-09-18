package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.ContactRequest;
import com.bxb.sunduk_pay.response.UserLoginResponse;
import com.bxb.sunduk_pay.response.UserResponse;

/**
 * Service interface for user-related operations such as login
 * and contact management.
 */
public interface UserService {

    /**
     * Handles user login or registration via provided user login response.
     *
     * @param response the user login response containing user details
     * @return the User entity after login or registration
     */
    User userLogin(UserLoginResponse response);

    /**
     * Uploads contacts for a user.
     *
     * @param contactRequest the request containing contact details
     * @return the response with updated user information
     */
    UserResponse uploadContacts(ContactRequest contactRequest);
}

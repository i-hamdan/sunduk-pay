package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.ContactRequest;
import com.bxb.sunduk_pay.response.UserLoginResponse;
import com.bxb.sunduk_pay.response.UserResponse;

/**
 * Service interface for user-related operations such as login and contact
 * management.
 */
public interface UserService {

    /**
     * Handles user login and returns user details.
     *
     * @param response The response object containing login details.
     * @return The user object corresponding to the logged-in user.
     */
    User userLogin(UserLoginResponse response);

    /**
     * Uploads contacts for the user.
     *
     * @param contactRequest The request object containing contact details.
     * @return The response object containing
     * the result of the upload operation.
     */
    UserResponse uploadContacts(ContactRequest contactRequest);
}

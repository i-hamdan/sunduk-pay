package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.UserRequest;
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
    User userLogin(UserResponse response);

    /**
     * Performs various user operations based on the provided request.
     *
     * @param request The request object containing operation details.
     * @return The response object containing the result of the operation.
     */
    UserResponse userOperations(UserRequest request);
}

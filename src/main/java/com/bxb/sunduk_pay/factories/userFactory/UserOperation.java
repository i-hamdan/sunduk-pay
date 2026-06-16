package com.bxb.sunduk_pay.factories.userFactory;

import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.util.UserRequestType;

/**
 * UserOperation defines the contract for user operations
 * based on different user request types.
 */
public interface UserOperation {
    /**
     * Returns the type of user request this operation handles.
     * @return UserRequestType
     */
UserRequestType getUserRequestType();
/**
     * Performs the user operation based on the provided request.
     * @param userRequest
     * @return UserResponse
     */
UserResponse perform(UserRequest userRequest);
}

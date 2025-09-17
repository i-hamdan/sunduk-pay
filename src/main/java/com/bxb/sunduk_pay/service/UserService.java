package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserLoginResponse;
import com.bxb.sunduk_pay.response.UserResponse;

import java.util.List;

public interface UserService {
    String createUser(UserRequest request);

    List<UserResponse> getAll();

    UserResponse getById(String id);

    String updateUser(String id, UserRequest request);

    String deleteUser(String id);

    void login(UserLoginResponse response);
}

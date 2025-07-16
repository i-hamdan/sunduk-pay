package com.example.walletApp.service;

import com.example.walletApp.request.UserRequest;
import com.example.walletApp.request.UserLoginRequest;
import com.example.walletApp.response.UserResponse;

import java.util.List;

public interface UserService {
    String createUser(UserRequest request);

    List<UserResponse> getAll();

    UserResponse getById(String id);

    String updateUser(String id, UserRequest request);

    UserResponse login(UserLoginRequest request);
}

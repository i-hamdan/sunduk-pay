package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserLoginResponse;
import com.bxb.sunduk_pay.response.UserResponse;

import java.util.List;

public interface UserService {

    void login(UserLoginResponse response);
}

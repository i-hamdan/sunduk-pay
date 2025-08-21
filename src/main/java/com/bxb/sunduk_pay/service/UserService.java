package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.response.UserLoginResponse;

public interface UserService {

    User userLogin(UserLoginResponse response);
}

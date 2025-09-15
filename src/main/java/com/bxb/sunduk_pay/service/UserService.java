package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.ContactRequest;
import com.bxb.sunduk_pay.response.UserLoginResponse;
import com.bxb.sunduk_pay.response.UserResponse;

public interface UserService {

    User userLogin(UserLoginResponse response);

    UserResponse uploadContacts(ContactRequest contactRequest);
}

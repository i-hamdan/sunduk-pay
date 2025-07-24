package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.util.UserType;
import org.springframework.stereotype.Service;

@Service
public class RegularUser implements UserRoleService{
    @Override
    public String getUserInfo() {
        return "This is a regular user.";
    }

    @Override
    public UserType getUserType() {
        return UserType.USER;
    }
}

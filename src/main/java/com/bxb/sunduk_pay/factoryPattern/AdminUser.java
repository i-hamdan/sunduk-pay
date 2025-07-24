package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.util.UserType;
import org.springframework.stereotype.Service;

@Service
public class AdminUser implements UserRoleService{
    @Override
    public String getUserInfo() {
        return "This is a admin.";
    }

    @Override
    public UserType getUserType() {
        return UserType.ADMIN;
    }
}

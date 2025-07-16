package com.example.walletApp.factoryPattern;

import com.example.walletApp.util.UserType;
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

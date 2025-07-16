package com.example.walletApp.request;

import com.example.walletApp.util.UserType;
import lombok.Data;

@Data
public class UserRequest {
    private String fullName;
    private String gender;
    private String email;
    private String phoneNumber;
    private String password;
    private UserType userType;
}

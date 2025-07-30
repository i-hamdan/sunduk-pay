package com.bxb.sunduk_pay.request;

import lombok.Data;

@Data
public class UserRequest {
    private String fullName;
    private String gender;
    private String email;
    private String phoneNumber;
    private String password;
}

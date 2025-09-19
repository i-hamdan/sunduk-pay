package com.bxb.sunduk_pay.response;

import lombok.*;

@Data
/**
 * Response object for user login containing user details.
 */
public class UserLoginResponse {
    private String uuid;
    private String email;
    private String fullName;
    private String phoneNumber;
}














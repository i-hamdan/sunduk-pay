package com.bxb.sunduk_pay.response;


import com.bxb.sunduk_pay.util.UserType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class UserResponse {
    private String uuid;
    private String fullName;
//    private String firstName;
//    private String lastName;
    private String gender;
    private String email;
    private String phoneNumber;
    private UserType userType;
}

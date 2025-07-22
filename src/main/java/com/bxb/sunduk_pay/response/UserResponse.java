package com.bxb.sunduk_pay.response;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserResponse {
    private String uuid;
    private String fullName;
//    private String firstName;
//    private String lastName;
    private String gender;
    private String email;
    private String phoneNumber;

}

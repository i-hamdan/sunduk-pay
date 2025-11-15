package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.UserRequestType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserRequest {
    private String uuid;
    private String email;
    private String fullName;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String mpin;
    private String presentAddress;
    private String permanentAddress;
    private UserRequestType userRequestType;
}

package com.example.walletApp.request;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Data
@ToString
public class UserLoginRequest {
    private String email;
    private String fullName;
}














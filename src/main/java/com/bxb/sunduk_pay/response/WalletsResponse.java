package com.bxb.sunduk_pay.response;


import com.bxb.sunduk_pay.model.User;
import lombok.*;

@Getter
@Setter
@ToString
public class WalletsResponse {
    private String walletId;
    private Double balance;
    private User user;
}

package com.bxb.sunduk_pay.response;

import com.bxb.sunduk_pay.model.User;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
public class WalletResponse {
    private String walletId;
    private Double balance;
    private User user;
    private List<TransactionResponse> transactionHistory;
}

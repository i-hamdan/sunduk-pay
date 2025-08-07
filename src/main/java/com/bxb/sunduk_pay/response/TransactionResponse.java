package com.bxb.sunduk_pay.response;

import com.bxb.sunduk_pay.util.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class TransactionResponse {
    private String uuid;
    private TransactionType transactionType;
    private Double amount;
    private String description;
    private LocalDateTime dateTime;
    private String walletId;
    private String fullName;
    private String subWalletId;
}

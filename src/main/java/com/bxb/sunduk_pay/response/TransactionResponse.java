package com.bxb.sunduk_pay.response;

import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class TransactionResponse {
    private String uuid;
    private TransactionType transactionType;
    private TransactionLevel transactionLevel;
    private Double amount;
    private String description;
    private LocalDateTime dateTime;
    private String subWalletId;
    private String walletId;
    private String fullName;
}

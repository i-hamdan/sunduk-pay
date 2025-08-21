package com.bxb.sunduk_pay.response;

import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {
    private String transactionId;
    private String uuid;
    private String mainWalletId;
    private String subWalletId;
    private TransactionType transactionType;
    private TransactionLevel transactionLevel;
    private Double amount;
    private String description;
    private LocalDateTime dateTime;
    private String fullName;
    private String fromWallet;
    private String fromWalletId;
    private String toWallet;
    private String toWalletId;

}

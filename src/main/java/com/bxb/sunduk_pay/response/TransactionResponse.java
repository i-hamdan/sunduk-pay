package com.bxb.sunduk_pay.response;

import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponse {
    private String transactionId;
    private String groupId;
    private String uuid;
    private String mainWalletId;
    private TransactionType transactionType;
    private TransactionLevel transactionLevel;
    private PaymentMethod paymentMethod;
    private Double amount;
    private String description;
    private String date;
    private String status;
    private String fullName;
    private String fromWallet;
    private String fromWalletId;
    private String fromWalletIcon;
    private String toWallet;
    private String toWalletId;
    private String toWalletIcon;
}

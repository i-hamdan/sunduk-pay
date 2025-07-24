package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.TransactionType;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class TransactionRequest {
    private String walletId;
    private Double amount;
    private TransactionType transactionType;
    private String description;
}

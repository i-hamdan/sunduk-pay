package com.bxb.sunduk_pay.event;

import com.bxb.sunduk_pay.util.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    private String transactionId;
    private String walletId;
    private Double amount;
    private TransactionType transactionType; // CREDIT / DEBIT
    private LocalDateTime dateTime;
    private Double remainingAmount;
    private String uuid;
    private String email;
    private String fullName;
    private String phoneNumber;
}

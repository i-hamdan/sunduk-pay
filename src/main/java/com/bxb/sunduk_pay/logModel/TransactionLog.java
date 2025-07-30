package com.bxb.sunduk_pay.logModel;

import com.bxb.sunduk_pay.util.TransactionType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Document
@Data
@Builder
public class TransactionLog {
    @Id
    private String logId;
    private String  transactionId;
    private String  walletId;
    private Double amount;
    private TransactionType transactionType; // CREDIT / DEBIT
    private LocalDateTime dateTime;
    private Double remainingAmount;
    private String uuid;
    private String email;
    private String fullName;
    private String phoneNumber;
}

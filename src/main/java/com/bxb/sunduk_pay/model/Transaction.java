package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.TransactionType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
public class Transaction {
    @Id
    private String transactionId;
    private TransactionType transactionType;
    private Double amount;
    private String description;
    private LocalDateTime dateTime;
    @DBRef
    private Wallet wallet;
    private Boolean isDeleted;
}

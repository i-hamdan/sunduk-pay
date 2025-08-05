package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    private String transactionId;
    private TransactionType transactionType;
    private TransactionLevel transactionLevel;
    private Double amount;
    private String description;
    private String status;
    private String stripePaymentIntentId;
    private LocalDateTime dateTime;
    private Boolean isDeleted;
    @DBRef
    private Wallet wallet;
    @DBRef
    private User user;
    private String subWalletId; //null when external transfer.
}

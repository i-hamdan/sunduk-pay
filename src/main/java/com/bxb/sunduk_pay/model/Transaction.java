package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
@Builder
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
private User user;
@DBRef
private MainWallet mainWallet;
private String fromWallet;
private String fromWalletId;
private String toWallet;
private String toWalletId;
private boolean isMaster;

}

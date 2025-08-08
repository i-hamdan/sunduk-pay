package com.bxb.sunduk_pay.model;

import jdk.jfr.Timestamp;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
@Builder
@Document
@Data
public class MainWallet {
    @Id
    private String mainWalletId;
    private Double balance;
    @Timestamp
    private LocalDateTime createdAt;
    @Timestamp
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
    @DBRef
    private User user;
    @DBRef
    private List<Transaction> transactionHistory;
    private List<SubWallet> subWallets;
}

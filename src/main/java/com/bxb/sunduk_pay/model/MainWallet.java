package com.bxb.sunduk_pay.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Builder
@Document
@Data
public class MainWallet {
    @Id
    private String mainWalletId;
    private Double balance;
    private Boolean isDeleted;
    @DBRef
    private User user;
    @DBRef
    private List<Transaction> transactionHistory;
    private List<SubWallet> subWallets;
}

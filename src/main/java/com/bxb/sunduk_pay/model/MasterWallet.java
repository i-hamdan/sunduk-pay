package com.bxb.sunduk_pay.model;

import jdk.jfr.Timestamp;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class MasterWallet {
    @Id
    private String masterWalletId;
    private Double balance;
    private Boolean isDeleted;
    @Timestamp
    private LocalDateTime createdAt;
    @DBRef
    private User user;
    @DBRef
    private MainWallet mainWallet;
}

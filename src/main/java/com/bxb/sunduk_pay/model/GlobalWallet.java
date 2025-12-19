package com.bxb.sunduk_pay.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class GlobalWallet {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private String globalWalletId;

    private Double balance;
    private LocalDateTime createdAt;
    private Boolean isActive;


}

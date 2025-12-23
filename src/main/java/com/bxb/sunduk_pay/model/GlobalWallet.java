package com.bxb.sunduk_pay.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class GlobalWallet {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private String globalWalletId;

    private Double balance;
    private Boolean isActive;

    @OneToOne(mappedBy = "globalWallet")
    private GlobalPot globalPot;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}

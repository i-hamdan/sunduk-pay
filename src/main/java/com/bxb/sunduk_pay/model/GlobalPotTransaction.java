package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity representing a transaction
 * related to a Global Pot.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalPotTransaction {

    /**
     * Unique identifier for the transaction.
     * Generated automatically using UUID strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String transactionId;

    /**
     * The global pot associated with this transaction.
     * This association is mandatory.
     */
    @ManyToOne(optional = false)
    private GlobalPot globalPot;

    /**
     * The global wallet associated with this transaction.
     * This association is mandatory.
     */
    @ManyToOne(optional = false)
    private GlobalWallet globalWallet;

    /**  amount involved in the transaction */
    private Double amount;

    /** type of the transaction: CREDIT or DEBIT */
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType; // CREDIT / DEBIT

/**     * The user who initiated this transaction.
     * This association is mandatory.
     */
    @ManyToOne(optional = false)
    private User user;

    /**  brief description of the transaction */
    private String description;

    /**  reference ID from the source transaction */
    private String sourceUserTransactionId;

    /**  date and time when the transaction occurred */
    private LocalDateTime dateTime;
}


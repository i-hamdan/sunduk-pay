package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.RiskLevel;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents an investment entity in the system.
 */
@Getter
@Setter
@Entity
public class Investment {
    /**
     * Unique identifier for the investment.
     */
    @Id
    private String InvestmentId;
    /**
     * The sub-wallet associated with the investment.
     */
    @ManyToOne
    private SubWallet subWallet;
    /**
     * The risk level associated with the investment.
     */
    private RiskLevel riskLevel;
    /**
     * The amount invested.
     */
    private Double investmentAmount;
    /**
    * The date when the investment matures.
    */
     private LocalDateTime investedAt;
    /**
    * The date when the investment was last updated.
    */
    private LocalDateTime updatedAt;
    /**
     * Indicates whether the investment is currently active.
     */
    private boolean isActive;


}


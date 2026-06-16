package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.RiskLevel;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents an investment entity in the system.
 */
@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "investments")
public class Investment {
    /**
     * Unique identifier for the investment.
     */
    @Id
    private String investmentId;
    /**
     * The sub-wallet associated with the investment.
     */
    @ManyToOne
    private SubWallet subWallet;

    /**
     * The user who made the investment.
     */
    @ManyToOne
    private User user;

    /**
     * The risk level associated with the investment.
     */
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;
    /**
     * The amount invested.
     */
    private Double investmentAmount;
    /**
     * The unit price at the time of purchase.
     */
    private Double unitPriceAtPurchase;     // price on that date

    /** The number of units purchased.
     */
    private Double units;

    /**
    * The date when the investment matures.
    */
    @Column(name = "investedAt", nullable = false)
    private LocalDate investedAt;
    /**
    * The date when the investment was last updated.
    */
    private LocalDateTime createdAt;
    /**
     * Indicates whether the investment is currently active.
     */
    private boolean isActive;
    /**
     * The current value of the investment.
     */
    private Double currentValue;

    /** Which portfolio model (LOW/MEDIUM/HIGH).
     * */

    private Long portfolioModelId;
    /**
     * The date associated with the asset.
     */
    private LocalDateTime unitPurchaseDate;

    /**
     * The profit or loss from the investment.
     */
    private Double profitLoss;

    /**
     * The profit or loss percentage from the investment.
     */
    private Double profitLossPercentage;
}


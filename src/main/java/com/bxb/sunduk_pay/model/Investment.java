package com.bxb.sunduk_pay.model;

import com.bxb.sunduk_pay.util.RiskLevel;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;

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
     * The unit price at the time of purchase.
     */
    private Double unitPriceAtPurchase;     // price on that date
    /**
     * The number of units purchased.
     */

    private Double units;// investedAmount / unitPriceAtPurchase

    /**
    * The date when the investment matures.
    */
     private LocalDate investedAt;
    /**
    * The date when the investment was last updated.
    */
    private LocalDateTime updatedAt;
    /**
     * Indicates whether the investment is currently active.
     */
    private boolean isActive;
    /**
     * The current value of the investment.
     */
    private Double currentValue;

}


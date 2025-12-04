package com.bxb.sunduk_pay.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Represents the daily historical data of an investment.
 */
@Entity
@Table(name = "investment_daily_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentDailyHistory {

    /**
     * Unique identifier for the investment daily history record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The investment associated with this daily history record.
     */
    @ManyToOne
    @JoinColumn(name = "investment_id", nullable = false)
    private Investment investment;

    /**
     * The user associated with this daily history record.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The date of the snapshot for this daily history record.
     */
    private LocalDate snapshotDate;
    /**
     * The number of units held on the snapshot date.
     */
    private Double units;
    /**
     * The unit price on the snapshot date.
     */
    private Double unitPrice;
    /**
     * The total invested amount on the snapshot date.
     */
    private Double currentValue;
    /**
     * The profit or loss in currency on the snapshot date.
     */
    private Double profitLoss;
    /**
     * The profit or loss percentage on the snapshot date.
     */
    private Double profitLossPercent;
}

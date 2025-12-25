package com.bxb.sunduk_pay.postgress.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing Units data for a Portfolio Model.
 *
 * This entity stores the calculated combined value of units
 * for a specific PortfolioModel on a given date.
 * It is typically used for tracking historical values,
 * snapshots, or performance calculations over time.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "units")
public class Units {

    /**
     * Unique identifier for the units record.
     * Generated automatically using an identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Date for which the unit value is recorded.
     */
    private LocalDate date;

    /**
     * Reference to the PortfolioModel associated with these units.
     * Multiple unit records can exist for the same portfolio model
     * across different dates.
     */
    @ManyToOne
    @JoinColumn(name = "model_id")
    private PortfolioModel portfolioModel;

    /**
     * Combined value of units for the given portfolio model and date.
     * Uses high precision and scale to support accurate
     * financial calculations.
     */
    @Column(name = "combined_value", precision = 30, scale = 15)
    private BigDecimal combinedValue;
}

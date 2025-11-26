package com.bxb.sunduk_pay.postgress.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing the allocation of assets within a portfolio model.
 */
@Entity
@Table(name = "portfolio_allocations")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class PortfolioAllocation {

    /** Primary key identifier for the portfolio allocation. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** The portfolio model to which this allocation belongs. */
    @ManyToOne
    @JoinColumn(name = "model_id")
    private PortfolioModel portfolioModel;

    /** The asset associated with this allocation. */
    @ManyToOne
    @JoinColumn(name = "asset_id")
    private Asset asset;

    /** The weight of the asset in the portfolio allocation. */
    private Double weight; // e.g. 0.2000
}

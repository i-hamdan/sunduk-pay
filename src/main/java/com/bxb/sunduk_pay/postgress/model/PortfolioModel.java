package com.bxb.sunduk_pay.postgress.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entity representing a portfolio model with its allocations.
 */
@Entity
@Table(name = "portfolio_models")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioModel {

    /** Unique identifier for the portfolio model. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /** Name of the portfolio model. */
    private String name;

    /** List of allocations associated with the portfolio model. */
    @OneToMany(mappedBy = "portfolioModel")
    private List<PortfolioAllocation> allocations;

}

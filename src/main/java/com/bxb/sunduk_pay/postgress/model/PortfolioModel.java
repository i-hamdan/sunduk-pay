package com.bxb.sunduk_pay.postgress.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Entity representing a portfolio model with its allocations.
 */
@Entity
@Table(name = "portfolio_models")
@Getter
@Setter
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
    @OneToMany(mappedBy = "portfolioModel",fetch = FetchType.EAGER)
    private List<PortfolioAllocation> allocations;

}

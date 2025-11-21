package com.bxb.sunduk_pay.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.time.LocalDate;
/**
 * Entity representing the unit prices of stocks
 * categorized by risk levels on a specific date.
 */
@Entity
@Table(name = "Stock_Unit_Price")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockUnitPrice {
/**
     * Unique identifier for the stock unit price entry.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
/**
     * The date for which the stock unit prices are recorded.
     */
    private LocalDate date;
/**
     * Unit price for low risk stocks.
     */
    private Double lowPrice;     // low risk unit price
   /**
     * Unit price for medium risk stocks.
     */
    private Double mediumPrice;  // medium risk unit price
    /**
     * Unit price for high risk stocks.
     */
    private Double highPrice;    // high risk unit price
}

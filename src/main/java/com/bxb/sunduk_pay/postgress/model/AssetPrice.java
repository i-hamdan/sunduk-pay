package com.bxb.sunduk_pay.postgress.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents the daily closing price for a specific asset at a given point in time.
 * This entity is crucial for tracking historical price data used for valuation and reporting.
 */
@Entity
@Table(name = "asset_prices")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetPrice {

    /**
     * Unique identifier for the asset price record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The exact date and time the price was recorded (e.g., market close time).
     * This field is often used in combination with the asset to ensure price uniqueness for a day.
     */
    @Column(name = "effective_at")
    private LocalDateTime effectiveAt; // CRITICAL: This field name must match
    // 'Date'
    // in your repository queries

    /**
     * The recorded closing price of the asset.
     * Uses BigDecimal for high precision (19 total digits, 4 decimal places)
     * to avoid floating-point errors in financial calculations.
     */
    @Column(name = "close_price", precision = 19, scale = 4) // Applied precision (19) and scale (4)
    private BigDecimal closePrice;

    /**
     * The {@link Asset} to which this price belongs.
     * This establishes a Many-to-One relationship, meaning one asset can have many price records.
     */
    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Asset asset;
}
package com.bxb.sunduk_pay.postgress.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
/**
 * Entity representing a financial Asset in the system.
 *
 * An asset defines a tradable or trackable financial instrument,
 * such as a stock, commodity, fund, or other investment type.
 * This entity stores basic identification and classification
 * details required for asset management.
 */
@Entity
@Table(name = "assets")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Asset {

    /**
     * Unique identifier for the asset.
     * Generated automatically using an identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Short symbol or ticker representing the asset.
     * Example: AAPL, BTC, GOLD.
     */
    private String symbol;

    /**
     * Human-readable name of the asset.
     * Example: Apple Inc., Bitcoin, Gold.
     */
    private String name;

    /**
     * Type or category of the asset.
     * Example: STOCK, CRYPTO, COMMODITY, FUND.
     */
    private String type;
}

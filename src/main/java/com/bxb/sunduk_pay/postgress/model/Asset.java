package com.bxb.sunduk_pay.postgress.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a financial asset in the system.
 */
@Entity
@Table(name = "assets")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Asset {
/**     * Unique identifier for the asset.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private String name;
    private String type;
}
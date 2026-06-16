package com.bxb.sunduk_pay.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a global landing page configuration.
 */
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "global_landing_page",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "config_key")
        }
)
public class GlobalLandingPage {
    /** Primary key identifier. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Configuration key for the landing page setting. */
    @Column(name = "config_key", nullable = false)
    private String key;

    /** Configuration value for the landing page setting. */
    @Column(name = "config_value", nullable = false)
    private String value;
}

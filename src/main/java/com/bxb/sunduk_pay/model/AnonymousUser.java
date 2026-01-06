package com.bxb.sunduk_pay.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "anonymous_user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"global_pot_id", "color_code"}),
                @UniqueConstraint(columnNames = {"global_pot_id", "user_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnonymousUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "anonymousId")
    private String anonymousId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "global_pot_id", nullable = false)
    private GlobalPot globalPot;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * This color IS the anonymous identity inside a pot
     */
    @Column(name = "color_code", nullable = false)
    private String colorCode;
}


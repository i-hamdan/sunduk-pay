package com.bxb.sunduk_pay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Entity representing a Mobile PIN (MPIN)
 * associated with a user.
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Mpin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /**
     * Unique identifier for the MPIN.
     */
    private Long pinId;
    /**
     * The mobile PIN value.
     */
    private String mpin;
    /**
     * The user associated with this MPIN.
     */
    @OneToOne
    @JoinColumn(name = "user_uuid", referencedColumnName = "uuid")
    private User user;
}


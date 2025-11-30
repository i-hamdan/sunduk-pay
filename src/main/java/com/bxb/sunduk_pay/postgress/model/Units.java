package com.bxb.sunduk_pay.postgress.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "units")
public class Units {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private PortfolioModel portfolioModel;

    @Column(name = "combined_value", precision = 30, scale = 15)
    private BigDecimal combinedValue;
}

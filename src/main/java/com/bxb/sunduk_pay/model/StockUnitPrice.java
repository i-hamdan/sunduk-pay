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

@Entity
@Table(name = "Stock_Unit_Price")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockUnitPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private Double lowPrice;     // low risk unit price
    private Double mediumPrice;  // medium risk unit price
    private Double highPrice;    // high risk unit price
}

package com.bxb.sunduk_pay.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvestmentGraphDataDTO {
    private LocalDateTime date;
    private Double balance;
}

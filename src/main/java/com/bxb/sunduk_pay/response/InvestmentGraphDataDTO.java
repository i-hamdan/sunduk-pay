package com.bxb.sunduk_pay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** * Data Transfer Object for investment graph data.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InvestmentGraphDataDTO {
    /** Raw date and time of the data point. */
    private LocalDateTime rawDateTime;
    /** Raw date of the data point. */
    private LocalDate rawDate;
    /** Formatted date string for display. */
    private String date;
    /** Balance value at the given date. */
    private Double balance;
}

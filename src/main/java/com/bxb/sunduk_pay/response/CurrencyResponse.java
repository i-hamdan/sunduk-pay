package com.bxb.sunduk_pay.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
   public class CurrencyResponse {
        private double exchangeRate;
        private double convertedAmount;
        private double conversionFee;
        private double finalAmount;
    }



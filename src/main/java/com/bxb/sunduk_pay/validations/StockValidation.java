package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.model.StockUnitPrice;

import java.time.LocalDate;

public interface StockValidation {
    StockUnitPrice getStockUnitPriceByDate(LocalDate date);
    void ValidateBalanceForInvestment(Double balance);
}

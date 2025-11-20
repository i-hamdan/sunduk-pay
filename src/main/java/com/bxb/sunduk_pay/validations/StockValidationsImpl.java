package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.model.StockUnitPrice;
import com.bxb.sunduk_pay.repository.StockUnitPriceRepository;
import com.bxb.sunduk_pay.request.InvestmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
@Component
@RequiredArgsConstructor
public class StockValidationsImpl implements StockValidation{

    private final StockUnitPriceRepository stockUnitPriceRepository;


    @Override
    public StockUnitPrice getStockUnitPriceByDate(LocalDate date) {
      return stockUnitPriceRepository.findByDate(date).orElseThrow(() ->
                new IllegalArgumentException("No stock unit price found for the given date: "
                        + date)
        );
    }
}

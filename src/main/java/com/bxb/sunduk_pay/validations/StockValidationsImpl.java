package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.exception.InsufficientBalanceException;
import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.model.StockUnitPrice;
import com.bxb.sunduk_pay.repository.StockUnitPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
/**
 * Implementation of stock-related validations.
 */
@Component
@RequiredArgsConstructor
public class StockValidationsImpl implements StockValidation{
/** Repository for stock unit prices. */
    private final StockUnitPriceRepository stockUnitPriceRepository;
/** Gets the stock unit price for a given date.
 * @param date the date for which to retrieve the stock unit price
 * @return the stock unit price for the specified date
 */

    @Override
    public StockUnitPrice getStockUnitPriceByDate(LocalDate date) {
      return stockUnitPriceRepository.findByDate(date).orElseThrow(() ->
                new ResourceNotFoundException("No stock unit price "
                        + "found for the given date: " + date)
        );
    }
    /** Validates if the balance is sufficient for investment.
     * @param balance the balance to validate
     */
    @Override
    public void ValidateBalanceForInvestment(Double balance) {
       if (balance <= 0|| balance == null) {
           throw new InsufficientBalanceException
                   ("Insufficient balance for investment.");
       }
    }
}

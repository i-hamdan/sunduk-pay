package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.exception.InsufficientBalanceException;
import com.bxb.sunduk_pay.exception.InvestmentException;
import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import com.bxb.sunduk_pay.postgress.model.Units;
import com.bxb.sunduk_pay.postgress.repository.PortfolioModelRepository;
import com.bxb.sunduk_pay.postgress.repository.UnitsRepository;
import com.bxb.sunduk_pay.repository.InvestmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of stock-related validations.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class StockValidationsImpl implements StockValidation {
    /**
     * Repository for investment data access.
     */
    private final InvestmentRepository investmentRepository;
    /**
     * Repository for portfolio model data access.
     */
    private final PortfolioModelRepository portfolioModelRepository;


    private final UnitsRepository unitsRepository;




    /**
     * Validates if the balance is sufficient for investment.
     *
     * @param balance the balance to validate
     */
    @Override
    public void ValidateBalanceForInvestment(Double balance) {
        if (balance <= 0 || balance == null) {
            throw new InsufficientBalanceException
                    ("Insufficient balance for investment.");
        }
    }

    @Override
    public PortfolioModel validatePortfolioModelByName(String name) {
        return portfolioModelRepository.findByName(name).orElseThrow(() ->
                new ResourceNotFoundException("Portfolio model not found with" +
                        " name: "
                        + name));
    }

   @Override
    public Investment getInvestmentBySubWalletId(String subWalletId) {
        return investmentRepository.findBySubWalletSubWalletIdAndIsActiveTrue(
                subWalletId).orElseThrow(
                () -> new InvestmentException("Cannot find active "
                        + "investment for this pot!"));
    }

    @Override
    public List<Investment> getInvestmentsByUserUuid(String uuid) {
        try {
            log.info("Fetching investments for user UUID: {}", uuid);
            List<Investment> investments = investmentRepository
                    .findByUserUuid(uuid);
            log.info("Fetched {} investments for user UUID: {}",
                    investments.size(), uuid);

            if (investments.isEmpty()) {
                log.error("No investments found for user UUID: {}",
                        uuid);
                throw new InvestmentException(
                        "No investments found for user with UUID: " + uuid);
            }

            return investments;

        } catch (Exception e) {
            throw new InvestmentException(
                    "Error fetching investments for user:"
                            + e.getMessage());
        }
    }

    @Override
    public Units getUnitsForDate(PortfolioModel model, LocalDate date) {
     // Try exact or nearest previous date
     List<Units> list = unitsRepository
                .findLatestBeforeOrOnDate(model, date);

     if (list.isEmpty()) {
        log.error("No unit value found for {} or any date before it",
                date);
         throw new ResourceNotFoundException(
        "No unit value found for date: " + date
            );
        }

        // RETURN CLOSEST UNIT RECORD
        return list.get(0);
    }

    @Override
    public Units findNextUnit(PortfolioModel model, LocalDate date) {
        List<Units> list = unitsRepository.findNextAfterDate(model, date);
        if (list.isEmpty()) {
            log.error("No unit value found for {} or any date before it",
                    date);
           return null;
        }
        Units nextUnit = list.get(0);

        log.info("Next unit found: {}  after {}",
                nextUnit.getDate(), date);

        return nextUnit;

    }
}

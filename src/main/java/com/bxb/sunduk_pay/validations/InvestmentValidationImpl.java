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
import com.bxb.sunduk_pay.util.RiskLevel;
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
public class InvestmentValidationImpl implements InvestmentValidation {
    /**
     * Repository for investment data access.
     */
    private final InvestmentRepository investmentRepository;
    /**
     * Repository for portfolio model data access.
     */
    private final PortfolioModelRepository portfolioModelRepository;

    /**
     * Repository for units data access.
     */
    private final UnitsRepository unitsRepository;




    /**
     * Validates if the balance is sufficient for investment.
     *
     * @param balance the balance to validate
     * @throws InsufficientBalanceException if the balance is insufficient.
     */
    @Override
    public void validateBalanceForInvestment(final Double balance) {
        if (balance <= 0 || balance == null) {
            throw new InsufficientBalanceException(
                    "Insufficient balance for investment.");
        }
    }

    /**
     * Validates and retrieves a PortfolioModel by its name.
     *
     * @param name the name of the portfolio model
     * @return the validated PortfolioModel
     * @throws ResourceNotFoundException if no portfolio model is found.
     */
    @Override
    public PortfolioModel validatePortfolioModelByName(final String name) {
        return portfolioModelRepository.findByName(name).orElseThrow(() ->
                new ResourceNotFoundException("Portfolio model not found with"
                        + " name: "
                        + name));
    }

    /**
     * Retrieves an active Investment by sub-wallet ID.
     *
     * @param subWalletId the sub-wallet ID
     * @return the active Investment
     * @throws InvestmentException if no active investment is found.
     */
   @Override
    public Investment getInvestmentBySubWalletId(final String subWalletId) {
        return investmentRepository.findBySubWalletSubWalletIdAndIsActiveTrue(
                subWalletId).orElseThrow(
                () -> new InvestmentException("Cannot find active "
                        + "investment for this pot!"));
    }

    /**
     * Retrieves investments by user UUID.
     *
     * @param uuid the user UUID
     * @return list of investments for the user
     */
    @Override
    public List<Investment> getInvestmentsByUserUuid(final String uuid) {
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

    /**
     * Retrieves the Units for a given PortfolioModel and date.
     *
     * @param model the PortfolioModel
     * @param date  the date to retrieve units for
     * @return the Units for the specified date
     */
    @Override
    public Units getUnitsForDate(final PortfolioModel model,
                                 final LocalDate date) {
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

    /**
     * Finds the next available Units after a given date.
     *
     * @param model the PortfolioModel
     * @param date  the date to find the next units after
     * @return the next Units after the specified date
     */
    @Override
    public Units findNextUnit(final PortfolioModel model,
                              final LocalDate date) {
        List<Units> list = unitsRepository.findNextAfterDate(model, date);
        if (list.isEmpty()) {
            log.error(
                    "No unit value found for {} or any date before it",
                    date);
           return null;
        }
        Units nextUnit = list.get(0);

        log.info("Next unit found: {}  after {}",
                nextUnit.getDate(), date);

        return nextUnit;

    }

    /**
     * Find unit by exact date.
     *
     * @param modelId PortfolioModel
     * @param date    LocalDate
     * @return Units
     */
    @Override
    public Units findUnitByDate(final Long modelId,
                                final LocalDate date) {
        try {
            return unitsRepository.findByPortfolioModelAndDate(modelId, date);
        } catch (Exception e) {
            log.error(
                    "Error fetching unit for model on date {}: {}",
                    date, e.getMessage());
            throw new InvestmentException(
                    "Error fetching unit for model on date: "
                            + e.getMessage());
        }
    }

    /**
     * Validates the provided risk level against
     * the current investment risk level.
     *
     * @param investmentRiskLevel the current investment risk level
     * @param riskLevel           the new risk level to validate
     * @return the validated RiskLevel
     */
    @Override
    public RiskLevel validateRiskLevel(final String investmentRiskLevel,
                                       final String riskLevel) {
        if (riskLevel.equalsIgnoreCase(investmentRiskLevel)) {
            throw new InvestmentException("No changes detected: "
                    + "the provided value is identical to the current value.");
        } else if (riskLevel.equalsIgnoreCase(RiskLevel.LOW.toString())) {
            return RiskLevel.LOW;
        } else if (riskLevel.equalsIgnoreCase(RiskLevel.MEDIUM.toString())) {
            return RiskLevel.MEDIUM;
        } else if (riskLevel.equalsIgnoreCase(RiskLevel.HIGH.toString())) {
            return RiskLevel.HIGH;
        } else {
            throw new InvestmentException(
                    "Invalid risk level: " + riskLevel);
        }
    }

    /**
     * Retrieves a PortfolioModel by risk level.
     *
     * @param riskLevel the risk level
     * @return the PortfolioModel for the specified risk level
     */
    @Override
    public PortfolioModel getPortfolioModelByRiskLevel(
            final String riskLevel) {
        return portfolioModelRepository.findByName(riskLevel).orElseThrow(
        () -> new InvestmentException("No portfolio model found for risk level:"
                        + riskLevel));
    }

    /**
     * Retrieves a PortfolioModel by its ID.
     *
     * @param id the ID of the PortfolioModel
     * @return the PortfolioModel with the specified ID
     */
    @Override
    public PortfolioModel getPortfolioModelById(final Long id) {
        return portfolioModelRepository.findById(id)
                .orElseThrow(() -> new InvestmentException(
                        "No portfolio model found for id: " + id));
    }
}

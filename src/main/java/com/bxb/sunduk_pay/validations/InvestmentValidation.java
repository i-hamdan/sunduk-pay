package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import com.bxb.sunduk_pay.postgress.model.Units;
import com.bxb.sunduk_pay.util.RiskLevel;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for stock-related validations.
 */
public interface InvestmentValidation {


    /** Validates if the balance is sufficient for investment.
     * @param balance the balance to validate
     */
    void validateBalanceForInvestment(Double balance);
    /** Validates and retrieves a PortfolioModel by its name.
     * @param name the name of the portfolio model
     * @return the validated PortfolioModel
     */
    PortfolioModel validatePortfolioModelByName(String name);

    /** Retrieve investment by sub-wallet ID.
     * @param subWalletId
     * @return
     */
    Investment getInvestmentBySubWalletId(String subWalletId);

    /** Retrieve investments by user UUID.
     * @param uuid
     * @return
     */
    List <Investment> getInvestmentsByUserUuid(String uuid);

    /** Retrieve units for a given portfolio model and date.
     * @param model
     * @param date
     * @return
     */
    Units getUnitsForDate(PortfolioModel model, LocalDate date);

    /** Find the next available units for a given portfolio model and date.
     * @param model
     * @param date
     * @return
     */
    Units findNextUnit(PortfolioModel model, LocalDate date);

    /** Find units by portfolio model ID and date.
     * @param modelId
     * @param date
     * @return
     */
    Units findUnitByDate(Long modelId, LocalDate date);

    /** Validate the risk level of an investment.
     * @param investmentRiskLevel
     * @param riskLevel
     * @return
     */
    RiskLevel validateRiskLevel(String investmentRiskLevel, String riskLevel);

    /** Retrieve a PortfolioModel by risk level.
     * @param riskLevel
     * @return
     */
    PortfolioModel getPortfolioModelByRiskLevel(String riskLevel);

    /** Retrieve a PortfolioModel by its ID.
     * @param id
     * @return
     */
    PortfolioModel getPortfolioModelById(Long id);
}

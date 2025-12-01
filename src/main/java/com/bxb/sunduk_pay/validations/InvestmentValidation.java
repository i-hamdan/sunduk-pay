package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import com.bxb.sunduk_pay.postgress.model.Units;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for stock-related validations.
 */
public interface InvestmentValidation {


    /** Validates if the balance is sufficient for investment.
     * @param balance the balance to validate
     */
    void ValidateBalanceForInvestment(Double balance);
    /** Validates and retrieves a PortfolioModel by its name.
     * @param name the name of the portfolio model
     * @return the validated PortfolioModel
     */
    PortfolioModel validatePortfolioModelByName(String name);

    /** Retrieve investment by sub-wallet ID. */
    Investment getInvestmentBySubWalletId(String subWalletId);

    List<Investment>getInvestmentsByUserUuid(String uuid);


    Units getUnitsForDate(PortfolioModel model, LocalDate date);


    Units findNextUnit(PortfolioModel model, LocalDate date);




}

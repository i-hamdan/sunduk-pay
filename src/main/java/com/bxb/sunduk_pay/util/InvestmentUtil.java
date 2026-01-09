package com.bxb.sunduk_pay.util;

import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.postgress.model.Units;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Utility class for updating Investment details
 * based on debit and credit transactions.
 */
@Component
@Log4j2
public class InvestmentUtil {
    /**
     * Updates the Investment details on a debit transaction.
     *
     * @param investment the Investment to update
     * @param unit       the Units containing NAV information
     * @param amount     the amount to debit
     * @return the updated Investment
     */
    public Investment updateInvestmentOnDebit(
            final Investment investment, Units unit ,Double amount) {
        double netAssetValue = unit.getCombinedValue().doubleValue();
        log.info("Current NAV for investment [{}] is {}",
                investment.getInvestmentId(), netAssetValue);

        if (netAssetValue <= 0) {
            throw new IllegalStateException("Invalid NAV value");
        }

        double unitsToDeduct = amount/netAssetValue;
        log.info("Units to deduct for payment of {} is {}",
                amount, unitsToDeduct);

        if (unitsToDeduct > investment.getUnits()) {
            unitsToDeduct = investment.getUnits();
        }

        double remainingUnits = investment.getUnits() - unitsToDeduct;
        log.info("Remaining units after deduction: {}",
                remainingUnits);

        double updatedCurrentValue = remainingUnits * netAssetValue;
        log.info("Updated current value after deduction: {}",
                updatedCurrentValue);

        double updatedInvestedAmount = investment.getInvestmentAmount()
                - amount;
        log.info("Updated invested amount after deduction: {}",
                updatedInvestedAmount);

        investment.setInvestmentAmount(updatedInvestedAmount);
        investment.setUnits(remainingUnits);
        investment.setCurrentValue(updatedCurrentValue);

        return investment;

    }

    /**
     * Updates the Investment details on a credit transaction.
     *
     * @param investment the Investment to update
     * @param unit       the Units containing NAV information
     * @param amount     the amount to credit
     * @return the updated Investment
     */
    public Investment updateInvestmentOnCredit(
            final Investment investment, Units unit, Double amount) {
        double netAssetValue = unit.getCombinedValue().doubleValue();
        log.info("Current NAV for investment [{}] is {}",
                investment.getInvestmentId(), netAssetValue);

        if (netAssetValue <= 0) {
            throw new IllegalStateException("Invalid NAV value");
        }


        double unitsToAdd = amount / netAssetValue;
        log.info("Units to add for amount of {} is {}",
                amount, unitsToAdd);

        double updatedUnits = investment.getUnits() + unitsToAdd;
        log.info("Updated units after addition: {}",
                updatedUnits);

        double updatedCurrentValue = updatedUnits * netAssetValue;
        log.info("Updated current value after addition: {}",
                updatedCurrentValue);

        double updatedInvestedAmount = investment.getInvestmentAmount()
                + amount;
        log.info("Updated invested amount after addition: {}",
                updatedInvestedAmount);

        investment.setInvestmentAmount(updatedInvestedAmount);
        investment.setUnits(updatedUnits);
        investment.setCurrentValue(updatedCurrentValue);

        return investment;
    }
}

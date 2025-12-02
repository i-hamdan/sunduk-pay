package com.bxb.sunduk_pay.scheduler;

import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.model.InvestmentDailyHistory;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import com.bxb.sunduk_pay.postgress.model.Units;
import com.bxb.sunduk_pay.repository.InvestmentDailyHistoryRepository;
import com.bxb.sunduk_pay.repository.InvestmentRepository;
import com.bxb.sunduk_pay.repository.SubWalletRepository;
import com.bxb.sunduk_pay.validations.InvestmentValidation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class ProfitAndLossCalculator {

    private final InvestmentRepository investmentRepository;
    private final SubWalletRepository subWalletRepository;
    private final InvestmentDailyHistoryRepository historyRepository;
    private final InvestmentValidation stockValidation;

    /**
     * DAILY P/L UPDATE based on NEXT AVAILABLE NAV (UNIT VALUE)
     */
    //@Scheduled(cron = "0 */2 * * * *")
    @Transactional
    public void calculateDailyprofitLoss() {

        log.info( "  Starting NEW UNIT-BASED Profit/Loss calculation...");

        List<Investment> investments = investmentRepository.findAllActiveInvestments();
        log.info("Active investments found: {}", investments.size());

        for (Investment inv : investments) {

            log.info("--------------------------------------------------");
            log.info("Processing Investment ID: {}", inv.getInvestmentId());

            Long modelId = inv.getPortfolioModelId();

            // lightweight model reference
            PortfolioModel model = new PortfolioModel();
            model.setId(modelId);

            //  USE Unit purchase date → REAL UNIT PURCHASE DATE
            LocalDate purchaseUnitDate = inv.getUnitPurchaseDate().toLocalDate();

            log.info("Unit Purchase Date  = {}", purchaseUnitDate);

            //  GET NEXT UNIT AFTER UNIT-PURCHASE-DATE
            Units nextUnit = stockValidation.findNextUnit(model, purchaseUnitDate);

            if (nextUnit == null) {
                log.warn("No next unit found for model {} after {}",
                        modelId, purchaseUnitDate);
                continue;
            }

            LocalDate NextUnitDate = nextUnit.getDate();

            double NextDateUnitValue = nextUnit.getCombinedValue().doubleValue();

            log.info("Next Unit Date = {} | Next Unit Value = {}",
                    NextUnitDate, NextDateUnitValue);

            // CALCULATE NEW VALUE
            double unitsHeld = inv.getUnits();
            double newValue = unitsHeld * NextDateUnitValue;

            // PROFIT / LOSS
            double investedAmount = inv.getInvestmentAmount();
            double profit = newValue - investedAmount;
            double profitPct = (profit / investedAmount) * 100;


            log.info("Units Held = {}", unitsHeld);
            log.info("Amount After p/l = {}", newValue);
            log.info("Profit = {}", profit);
            log.info("Profit % = {}", profitPct);

            // UPDATE INVESTMENT
            inv.setCurrentValue(newValue);
            inv.setProfitLoss(profit);
            inv.setProfitLossPercentage(profitPct);

            // IMPORTANT → move Unit forward
            inv.setUnitPurchaseDate(NextUnitDate.atStartOfDay());

            inv.setUpdatedAt(LocalDateTime.now());
            investmentRepository.save(inv);

            //  Update SubWallet balance
            SubWallet wallet = inv.getSubWallet();
            wallet.setBalance(newValue);
            subWalletRepository.save(wallet);

            //  SAVE DAILY HISTORY
            historyRepository.save(
                    InvestmentDailyHistory.builder()
                            .investment(inv)
                            .snapshotDate(NextUnitDate)
                            .units(unitsHeld)
                            .unitPrice(NextDateUnitValue)
                            .currentValue(newValue)
                            .profitLoss(profit)
                            .profitLossPercent(profitPct)
                            .build()
            );

            log.info("Saved history for investment {}", inv.getInvestmentId());
        }

        log.info(" Profit/Loss calculation completed successfully.");
    }
}

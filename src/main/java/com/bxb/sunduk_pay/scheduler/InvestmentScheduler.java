package com.bxb.sunduk_pay.scheduler;

import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.model.InvestmentDailyHistory;
import com.bxb.sunduk_pay.model.StockUnitPrice;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.repository.InvestmentDailyHistoryRepository;
import com.bxb.sunduk_pay.repository.InvestmentRepository;
import com.bxb.sunduk_pay.repository.StockUnitPriceRepository;
import com.bxb.sunduk_pay.repository.SubWalletRepository;
import com.bxb.sunduk_pay.util.RiskLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Scheduler component for handling investment-related scheduled tasks.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class InvestmentScheduler {
    /**
     * Repository for managing investments.
     **/
    private final InvestmentRepository investmentRepository;
    /**
     * Repository for managing stock unit prices.
     **/
    private final StockUnitPriceRepository stockUnitPriceRepository;
    /**
     * Repository for managing investment daily histories.
     **/
    private final InvestmentDailyHistoryRepository historyRepository;
    /**
     * Repository for managing sub-wallets.
     **/
    private final SubWalletRepository subWalletRepository;


    public void calculateDailyprofitLoss() {

        LocalDate today = LocalDate.now();
        log.info("Investment daily profit/loss calculation started for date: {}"
                , today);
        // 1. Get latest or today's unit price
        StockUnitPrice price = stockUnitPriceRepository
                .findByDate(today)
                .orElseGet(() -> stockUnitPriceRepository.findLatest().orElse(null));

        if (price == null) {
            log.warn(" No StockUnitPrice found. Skipping calculation " +
                    "calculation.");
            return;
        }

        log.info
                (" Using StockUnitPrice for date {} -> Low: {}, Medium: {}, " +
                                "High: {}",
                        price.getDate(), price.getLowPrice(),
                        price.getMediumPrice(), price.getHighPrice());


        // 2. Get all active investments
        List<Investment> investments =
                investmentRepository.findByIsActiveTrue();

        log.info(" Found {} active investments to process",
                investments.size());

        for (Investment investment : investments) {

            // skip if already processed today
            if (historyRepository.existsByInvestmentAndSnapshotDate(investment,
                    today)) {
                log.info(" History already exists for investment {} on {}",
                        investment.getInvestmentId(), today);
                continue;
            }
            double units = investment.getUnits();

            double invested = investment.getInvestmentAmount();
            RiskLevel risk = investment.getRiskLevel();

            // 3. pick unit price based on risk
            double unitPrice = switch (risk) {
                case LOW -> price.getLowPrice();
                case MEDIUM -> price.getMediumPrice();
                case HIGH -> price.getHighPrice();
            };

            double currentValue = units * unitPrice;
            double profitLoss = currentValue - invested;
            double percent = invested > 0 ? (profitLoss / invested) * 100 : 0.0;

            log.info(""" 
       Investment {}: Units: {}, Invested: {},\s
       UnitPrice: {}, CurrentValue: {}, P/L: {}, P/L%: {}
       """,
                    investment.getInvestmentId(), units, invested,
                    unitPrice, currentValue, profitLoss, percent);

            // 4. update pot(SubWallet) balance

            investment.setCurrentValue(currentValue);

            SubWallet pot = investment.getSubWallet();
            pot.setBalance(currentValue);
            subWalletRepository.save(pot);
            log.info(" Updated SubWallet {} balance to {}",
                    pot.getSubWalletId(), currentValue);

            // 5. save daily history
            InvestmentDailyHistory history = InvestmentDailyHistory.builder()
                    .investment(investment)
                    .snapshotDate(today)
                    .units(units)
                    .unitPrice(unitPrice)
                    .currentValue(currentValue)
                    .profitLoss(profitLoss)
                    .profitLossPercent(percent)
                    .build();

            historyRepository.save(history);
            log.info(" Daily history saved for investment {} on {}",
                    investment.getInvestmentId(), today);

        }


    }


}


package com.bxb.sunduk_pay.scheduler;

import com.bxb.sunduk_pay.repository.InvestmentDailyHistoryRepository;
import com.bxb.sunduk_pay.repository.InvestmentRepository;
import com.bxb.sunduk_pay.repository.StockUnitPriceRepository;
import com.bxb.sunduk_pay.repository.SubWalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

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





    }


}


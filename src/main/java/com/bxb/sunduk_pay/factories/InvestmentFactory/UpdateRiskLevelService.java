package com.bxb.sunduk_pay.factories.InvestmentFactory;

import com.bxb.sunduk_pay.exception.InvestmentException;
import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.model.InvestmentDailyHistory;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import com.bxb.sunduk_pay.postgress.model.Units;
import com.bxb.sunduk_pay.repository.InvestmentDailyHistoryRepository;
import com.bxb.sunduk_pay.repository.InvestmentRepository;
import com.bxb.sunduk_pay.repository.SubWalletRepository;
import com.bxb.sunduk_pay.request.InvestmentRequest;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import com.bxb.sunduk_pay.util.InvestmentRequestType;
import com.bxb.sunduk_pay.util.RiskLevel;
import com.bxb.sunduk_pay.validations.InvestmentValidation;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Service to handle updating the risk level of an investment.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class UpdateRiskLevelService implements InvestmentOperation {

    /**
     * Utility for validations.
     */
    private final Validations validations;

    /**
     * Utility for investment-specific validations.
     */
    private final InvestmentValidation investmentValidation;

    /**
     * Repository for investment persistence.
     */
    private final InvestmentRepository investmentRepository;
 /**
     * Repository for investment daily history persistence.
     */
    private final InvestmentDailyHistoryRepository dailyHistory;

/**
     * Repository for sub-wallet persistence.
     */
    private final SubWalletRepository subWalletRepository;


    /**
     * Returns the InvestmentRequestType handled by this service.
     *
     * @return InvestmentRequestType.CHANGE_RISK_LEVEL
     */
    @Override
    public InvestmentRequestType getInvestmentRequestType() {
        return InvestmentRequestType.CHANGE_RISK_LEVEL;
    }

    /**
     * Updates the risk level of an investment associated with a sub-wallet.
     *
     * @param investmentRequest request containing user UUID,
     *                          sub-wallet ID, and new risk level
     * @return response indicating success
     */
    @Override
    public InvestmentResponse perform(
            final InvestmentRequest investmentRequest) {

        long startTime = System.currentTimeMillis();
        log.info("Before User Validation : 0ms");
        User user = validations.getUserInfo(investmentRequest.getUuid());
        log.info("User validation successful for UUID: {}",
                investmentRequest.getUuid());
        
        long endTime = System.currentTimeMillis();
        log.info("After User Validation : {}ms",
                endTime - startTime);

        SubWallet subWallet = validations.findSubWalletIfExists(
                user.getMainWallet().getMainWalletId(),
                investmentRequest.getSubWalletId());
        log.info("SubWallet validation successful for ID: {}",
                investmentRequest.getSubWalletId());
        
        endTime = System.currentTimeMillis();
        log.info("After SubWallet Validation : {}ms",
                endTime - startTime);


        Investment investment = investmentValidation.
                getInvestmentBySubWalletId(subWallet.getSubWalletId());

        log.info("Investment retrieval successful for SubWallet ID: {}",
                investmentRequest.getSubWalletId());
        
        endTime = System.currentTimeMillis();
        log.info("After Investment Validation : {}ms",
                endTime - startTime);

        if (!subWallet.getIsInvested()
                || !investment.isActive()) {
            throw new InvestmentException(
                    "Cannot change risk level for inactive investment.");
        }


        RiskLevel riskLevel = investmentValidation
                .validateRiskLevel(investment.getRiskLevel().toString(),
                        investmentRequest.getRiskLevel()
                        .toString());


        subWallet.setRiskLevel(riskLevel);
        
        endTime = System.currentTimeMillis();
        log.info("Risk level validation successful : {}ms",
                endTime - startTime);

        log.info("Risk level validation successful. Changing from {} to {}",
                investment.getRiskLevel().toString(),
                investmentRequest.getRiskLevel().toString());

        PortfolioModel portfolioModel = investmentValidation
                .getPortfolioModelByRiskLevel(riskLevel.toString());
        log.info("Portfolio model retrieval successful for risk level: {}",
                riskLevel.toString());
        
        endTime = System.currentTimeMillis();
        log.info("After Portfolio Model Retrieval : {}ms",
                endTime - startTime);


        InvestmentDailyHistory lastSnapshot = dailyHistory
                .findTopByInvestmentOrderBySnapshotDateDesc(investment);

        endTime = System.currentTimeMillis();
        log.info("After Fetching Investment Daily History : {}ms",
                endTime - startTime);

        LocalDate snapshotDate;

        if (lastSnapshot != null) {
            snapshotDate = lastSnapshot.getSnapshotDate();
            log.info("Using last P/L snapshot date: {}", snapshotDate);
        } else {
            snapshotDate = investment.getCreatedAt().toLocalDate();
            log.info(
                    "No P/L snapshot found. Using investment creation date: {}",
                    snapshotDate
            );
        }


        log.info("Using snapshot date for NAV alignment: {}", snapshotDate);


        Units newModelUnit = investmentValidation
                .getUnitsForDate(portfolioModel, snapshotDate);
        
        endTime = System.currentTimeMillis();
        log.info("After Fetching Units for Portfolio Model : {}ms",
                 endTime - startTime);


        double newUnitValue = newModelUnit.getCombinedValue().doubleValue();

        log.info("New model NAV on {} = {}", snapshotDate, newUnitValue);


        double currentValue = investment.getCurrentValue();
        double recalculatedUnit = currentValue / newUnitValue;


        investment.setUnits(recalculatedUnit);
        investment.setPortfolioModelId(portfolioModel.getId());
        investment.setRiskLevel(riskLevel);

        investmentRepository.save(investment);
        subWalletRepository.save(subWallet);
        
        endTime = System.currentTimeMillis();
        log.info("After Saving Updated Investment and SubWallet : {}ms",
        endTime - startTime);
        return InvestmentResponse.builder()
                .message("Risk level updated successfully to "
                        + investmentRequest.getRiskLevel()
                        .toString())
                .build();
    }
}

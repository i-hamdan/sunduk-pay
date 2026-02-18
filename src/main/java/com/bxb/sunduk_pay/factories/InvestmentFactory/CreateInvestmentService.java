package com.bxb.sunduk_pay.factories.InvestmentFactory;

import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import com.bxb.sunduk_pay.postgress.model.Units;
import com.bxb.sunduk_pay.repository.InvestmentRepository;
import com.bxb.sunduk_pay.repository.SubWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.InvestmentRequest;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import com.bxb.sunduk_pay.util.InvestmentRequestType;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.InvestmentValidation;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * NEW INVESTMENT CREATION LOGIC USING:
 * 10-month old UNIT VALUE instead of today's NAV.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class CreateInvestmentService implements InvestmentOperation {

    /** Constant for ten. */
    private static final int TEN = 10;
    /** validations class for subWallet.*/
    private final Validations validations;
    /** validation for investment Validations. */
    private final InvestmentValidation investmentValidations;
    /** subWallet repository for saved flag. */
    private final SubWalletRepository subWalletRepository;
    /** Investment repository for saved mark as subWallet is invested. */
    private final InvestmentRepository investmentRepository;
    /** Transaction repository for saved transaction. */
    private final TransactionRepository transactionRepository;


    /**
     * Returns the InvestmentRequestType handled by this service.
     *
     * @return InvestmentRequestType.CREATE_INVESTMENT
     */
    @Override
    public InvestmentRequestType getInvestmentRequestType() {
        return InvestmentRequestType.CREATE_INVESTMENT;
    }

    /**
     * Perform new investment creation.
     *
     * @param request the investment request
     * @return the investment response
     */
    @Transactional
    @Override
    public InvestmentResponse perform(final InvestmentRequest request) {

        long startTime = System.currentTimeMillis();
        log.info("Starting investment creation process for request: 0ms");
        // 1) User fetch
        User user = validations.getUserInfo(request.getUuid());
        
        long endTime = System.currentTimeMillis();
        log.info("After Validation for User : {}ms"
                ,endTime-startTime);

        // 2) SubWallet fetch + validate ownership
        SubWallet subWallet =
                validations.findSubWalletIfExists(
                        user.getMainWallet().getMainWalletId(),
                        request.getSubWalletId()
                );

        log.info("Verifying sub-wallet: {}", subWallet);
        
        endTime = System.currentTimeMillis();
        log.info("Verifying sub-wallet : {} ms"
                ,endTime-startTime);

        // 3) Balance validation
        investmentValidations.validateBalanceForInvestment(
                subWallet.getBalance());
        
        endTime = System.currentTimeMillis();
        log.info("Balance Validation : {} ms",endTime-startTime);

        // 4) Ensure subwallet is not already invested
        validations.validateSubWalletForInvestment(subWallet);
        
            endTime = System.currentTimeMillis();
            log.info("Sub-Wallet Investment Validation : {} ms",
                    endTime-startTime);


        // 5) Fetch portfolio model by risk level
        PortfolioModel portfolioModel =
                investmentValidations.validatePortfolioModelByName(
                        request.getRiskLevel().name());

        log.info("Fetched Portfolio Model: {}", portfolioModel.getName());
        
        endTime = System.currentTimeMillis();
        log.info("Portfolio Model Validation : {} ms"
                ,endTime-startTime);

        // Total money user is investing
        double potAmount = subWallet.getBalance();

        // 6)  BUY using 6-MONTH OLD UNIT VALUE
        LocalDate unitPurchaseDate = LocalDate.now().minusMonths(
                TEN);

        // get Units of date
        Units unitRecord =
               investmentValidations.getUnitsForDate(
                       portfolioModel, unitPurchaseDate);
        
        endTime = System.currentTimeMillis();
        log.info("Unite Value Validation : {} ms"
                ,endTime-startTime);


        double unitValue = unitRecord.getCombinedValue().doubleValue();

        log.info(" BUY UNIT VALUE FOR DATE = {} UNIT {} | Model = {}",
                unitValue, portfolioModel.getName(), unitRecord);

        // 7) Units purchased = amount / unitValue
        double unitsPurchased = potAmount / unitValue;

        log.info("Units Purchased = {} / {} = {}", potAmount,
                unitValue, unitsPurchased);

        // 8) Create Investment object
        Investment investment = Investment.builder()
                .investmentId(UUID.randomUUID().toString())
                .user(user)
                .subWallet(subWallet)
                .portfolioModelId(portfolioModel.getId())
                .riskLevel(request.getRiskLevel())
                .investmentAmount(potAmount)
                .unitPriceAtPurchase(unitValue)
                .units(unitsPurchased)
                .investedAt(LocalDate.now())
                .unitPurchaseDate(unitPurchaseDate.atStartOfDay())
                .createdAt(LocalDateTime.now())
                .currentValue(potAmount)
                .profitLoss(0.0)
                .profitLossPercentage(0.0)
                .isActive(true)
                .build();

        investmentRepository.save(investment);
        
        endTime = System.currentTimeMillis();
        log.info("Investment Creation and Save : {} ms"
        ,endTime-startTime);

        // 9) Mark wallet as invested
        subWallet.setIsInvested(true);
        subWallet.setIsCancelInvestment(false);
        subWallet.setRiskLevel(request.getRiskLevel());


        subWalletRepository.save(subWallet);
        
        endTime = System.currentTimeMillis();
        log.info("Sub-Wallet Update : {} ms",endTime-startTime);

        // 10) Create transaction history
        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .user(user)
                .amount(potAmount)
                .transactionType(TransactionType.DEBIT)
                .transactionLevel(TransactionLevel.INVESTED)
                .dateTime(LocalDateTime.now())
                .description("Invested " + potAmount
                        + " using model " + portfolioModel.getName())
                .fromWallet(subWallet.getSubWalletName())
                .fromWalletId(subWallet.getSubWalletId())
                .toWallet(
                        "INVESTMENT")
                .isInvestment(true)
                .riskLevel(investment.getRiskLevel())
                .isMaster(false)
                .build();

        transactionRepository.save(transaction);
        
        endTime = System.currentTimeMillis();
        log.info("After Transaction Creation : {} ms"
                ,endTime-startTime);

        return InvestmentResponse.builder()
                .message(
                     "Great Job! Your investment is now active and growing.")
                .isCancelInvestment(false)
                .build();
    }
}

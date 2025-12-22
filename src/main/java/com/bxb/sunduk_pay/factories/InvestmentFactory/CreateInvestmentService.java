package com.bxb.sunduk_pay.factories.InvestmentFactory;

import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
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
 * 10-month old UNIT VALUE instead of today's NAV
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class CreateInvestmentService implements InvestmentOperation {
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


    @Override
    public InvestmentRequestType getInvestmentRequestType() {
        return InvestmentRequestType.CREATE_INVESTMENT;
    }

    @Transactional
    @Override
    public InvestmentResponse perform(InvestmentRequest request) {

        // 1) User fetch
        User user = validations.getUserInfo(request.getUuid());

        // 2) SubWallet fetch + validate ownership
        SubWallet subWallet =
                validations.findSubWalletIfExists(
                        user.getMainWallet().getMainWalletId(),
                        request.getSubWalletId()
                );

        log.info("Verifying sub-wallet: {}", subWallet);

        // 3) Balance validation
        investmentValidations.ValidateBalanceForInvestment(subWallet.getBalance());

        // 4) Ensure subwallet is not already invested
        validations.validateSubWalletForInvestment(subWallet);


        // 5) Fetch portfolio model by risk level
        PortfolioModel portfolioModel =
                investmentValidations.validatePortfolioModelByName
                        (request.getRiskLevel().name());

        log.info("Fetched Portfolio Model: {}", portfolioModel.getName());

        // Total money user is investing
        double potAmount = subWallet.getBalance();

        // 6)  BUY using 6-MONTH OLD UNIT VALUE
        LocalDate unitPurchaseDate = LocalDate.now().minusMonths(10);

        // get Units of date
        Units unitRecord =
               investmentValidations.getUnitsForDate(portfolioModel,unitPurchaseDate);


        double unitValue = unitRecord.getCombinedValue().doubleValue();

        log.info(" BUY UNIT VALUE FOR DATE = {} UNIT {} | Model = {}",
                unitValue, portfolioModel.getName(),unitRecord);

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
                .UnitPurchaseDate(unitPurchaseDate.atStartOfDay())
                .createdAt(LocalDateTime.now())
                .currentValue(potAmount)
                .profitLoss(0.0)
                .profitLossPercentage(0.0)
                .isActive(true)
                .build();

        investmentRepository.save(investment);

        // 9) Mark wallet as invested
        subWallet.setIsInvested(true);
        subWallet.setRiskLevel(request.getRiskLevel());


        subWalletRepository.save(subWallet);

        // 10) Create transaction history
        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .user(user)
                .amount(potAmount)
                .transactionType(TransactionType.DEBIT)
                .transactionLevel(TransactionLevel.INVESTED)
                .dateTime(LocalDateTime.now())
                .description("Invested " + potAmount +
                        " using model " + portfolioModel.getName())
                .fromWallet(subWallet.getSubWalletName())
                .fromWalletId(subWallet.getSubWalletId())
                .toWallet("Investment")
                .isInvestment(true)
                .riskLevel(investment.getRiskLevel())
                .isMaster(false)
                .build();

        transactionRepository.save(transaction);

        return InvestmentResponse.builder()
                .message("Great Job! Your investment is now active and growing.")
                .build();
    }
}

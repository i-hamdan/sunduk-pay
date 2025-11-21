package com.bxb.sunduk_pay.factories.InvestmentFactory;
import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.model.StockUnitPrice;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.InvestmentRepository;
import com.bxb.sunduk_pay.repository.StockUnitPriceRepository;
import com.bxb.sunduk_pay.repository.SubWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.InvestmentRequest;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import com.bxb.sunduk_pay.util.InvestmentRequestType;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.StockValidation;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for creating investments.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class CreateInvestmentService implements InvestmentOperation {
    /**
     * Repository for stock unit prices.
     */
    private final StockUnitPriceRepository stockUnitRepository;
    /**
     * Validation utilities.
     */
    private final Validations validations;
    /**
     * Stock-specific validation utilities.
     */
    private final StockValidation stockValidation;
    /**
     * Repository for sub-wallets.
     */
    private final SubWalletRepository subWalletRepository;
    /**
     * Repository for InvestmentRepository.
     */
    private final InvestmentRepository investmentRepository;
    /**
     * Repository for transactions.
     */
    private final TransactionRepository transactionRepository;
    /**
     * Gets the type of investment request this service handles.
     *
     * @return InvestmentRequesType.CREATE_INVESTMENT
     */
    @Override
    public InvestmentRequestType getInvestmentRequestType() {
        return InvestmentRequestType.CREATE_INVESTMENT;
    }
    /**
     * Performs the investment creation operation based on the provided request.
     *
     * @param request the investment request
     * @return the investment response
     */

    @Transactional
    @Override
    public InvestmentResponse perform(InvestmentRequest request) {


        User user = validations.getUserInfo(request.getUuid());

        SubWallet subWallet =
                validations.findSubWalletIfExists(user.getMainWallet()
                                .getMainWalletId(),
                        request.getSubWalletId());
        log.info("verifying sub-wallet: " + subWallet);

        stockValidation.ValidateBalanceForInvestment(subWallet.getBalance());
        log.info("Balance validation passed for sub-wallet: "
                + subWallet.getSubWalletName());

        if (subWallet != null && !subWallet.getIsInvested()) {

            log.info("Creating investment for sub-wallet: "
                    + subWallet.getSubWalletName());

            subWallet.setIsInvested(true);

            StockUnitPrice price =
                    stockValidation.getStockUnitPriceByDate(request
                    .getInvestedAt());
                    log.info(price);

                    log.info("Determining unit price based on risk level: "
                    + request.getRiskLevel());

            double unitPrice =
                    switch (request.getRiskLevel()) {
                        case LOW -> price.getLowPrice();
                        case MEDIUM -> price.getMediumPrice();
                        case HIGH -> price.getHighPrice();
                    };

            log.info("Unit price determined: " + unitPrice);
            double units = subWallet.getBalance() / unitPrice;

            log.info("Units to be purchased: " + units);
            Investment investment = Investment.builder()
                    .InvestmentId(UUID.randomUUID().toString())
                    .subWallet(subWallet)
                    .currentValue(subWallet.getBalance())
                    .riskLevel(request.getRiskLevel())
                    .investmentAmount(subWallet.getBalance())
                    .unitPriceAtPurchase(unitPrice)
                    .units(units)
                    .investedAt(request.getInvestedAt())
                    .isActive(true)
                    .build();

            Transaction transaction = Transaction.builder().transactionId(UUID
                     .randomUUID().toString())
                    .user(user)
                    .isMaster(false)
                    .amount(subWallet.getBalance())
                    .transactionType(TransactionType.DEBIT)
                    .transactionLevel(TransactionLevel.INVESTED)
                    .dateTime(LocalDateTime.now())
                    .fromWalletId(subWallet.getSubWalletId())
                    .fromWallet(subWallet.getSubWalletName())
                    .toWallet("Investment")
                    .description("Investment of amount "
                            + subWallet.getBalance()
                            + " from Sub-Wallet: "
                            + subWallet.getSubWalletName()
                            + " to Investment.")
                    .build();

            investmentRepository.save(investment);
            subWalletRepository.save(subWallet);
            transactionRepository.save(transaction);

            return InvestmentResponse.builder()
                    .message(" Great Job !"
                            + " your Investment is now active and growing.")
                    .build();

        } else {
            throw new IllegalArgumentException("Investment cannot be created. "
                    + "Either the sub-wallet does not exist or it is already "
                    + "invested.");
        }
    }

}

package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.encryption.MpinValidations;
import com.bxb.sunduk_pay.exception.InvestmentException;
import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import com.bxb.sunduk_pay.postgress.model.Units;
import com.bxb.sunduk_pay.repository.InvestmentRepository;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.InternalTransferService;
import com.bxb.sunduk_pay.util.InvestmentUtil;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.InvestmentValidation;
import com.bxb.sunduk_pay.validations.Validations;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Service implementation for performing internal transfers between wallets.
 * Handles balance deduction, transaction creation, goal milestone checks, and
 * Kafka event publishing.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class InternalTransferServiceImpl implements InternalTransferService {

    /**
     * 50% as a decimal for calculations.
     */
    private static final double FIFTY_PERCENT = 0.5;
    /**
     * Seveny-five percent as a decimal for calculations.
     */
    private static final double SEVENTY_FIVE_PERCENT = 0.75;
    /**
     * 50% goal completion threshold.
     */
    private static final int GOAL_50_PERCENT = 50;
    /**
     * 75% goal completion threshold.
     */
    private static final int GOAL_75_PERCENT = 75;
    /**
     * 100% goal completion threshold.
     */
    private static final int GOAL_100_PERCENT = 100;
    /**
     * Validations utility for MPIN checks.
     */
    private final MpinValidations mpinValidations;
    /**
     * Validations utility for business rule enforcement.
     */
    private final Validations validations;
    /**
     * Repository for transaction persistence.
     */
    private final TransactionRepository transactionRepository;
    /**
     * Repository for main wallet persistence.
     */
    private final MainWalletRepository mainWalletRepository;
    /**
     * Mapper for converting transactions to events.
     */
    private final TransactionMapper transactionMapper;
    /**
     * Kafka template for publishing transaction events.
     */
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    /**
     * Repository for investment data access.
     */
    private final InvestmentRepository investmentRepository;
    /**
     * Validation for investment operations.
     */
    private final InvestmentValidation investmentValidation;

    /**
     * Utility for investment-related operations.
     */
    private final InvestmentUtil investmentUtil;


    /**
     * Performs an internal transfers.
     * Between wallets or sub-wallets for a user.
     *
     * @param user                        the user performing transfer
     * @param mainWallet                  user's main wallet
     * @param amount                      transfer amount
     * @param sourceWallet                source wallet
     * @param targetWallet                target wallet
     * @param previousSourceWalletBalance previous source balance
     * @param previousTargetWalletBalance previous target balance
     * @param mpin                        user's MPIN for validation
     * @return MainWalletResponse with transaction details
     */
    @Transactional
    public MainWalletResponse doInternalTransfer(
            final User user,
            final MainWallet mainWallet,
            final Double amount,
            final WalletWrapper sourceWallet,
            final WalletWrapper targetWallet,
            final Double previousSourceWalletBalance,
            final Double previousTargetWalletBalance,
            final String mpin) {
        try {
            long startTime = System.currentTimeMillis();
            log.info("Internal transfer DB & validation started at : 0ms");
            log.info(
               "Starting internal transfer of amount {} from {} to {}",
                    amount, sourceWallet.getId(), targetWallet.getId());

            List<Transaction> transactions = new ArrayList<>();

            log.debug(
                    "Validating source wallet balance: currentBalance="
                            + sourceWallet.getBalance()
                            + ", transferAmount=" + amount);
            
            validations.validateBalance(sourceWallet.getBalance(), amount);
            long endTime = System.currentTimeMillis();
            log.info("Balance validation successful {}",
                    endTime - startTime+"ms");

            log.info(
                    "Deducting {} from source wallet {}",
                    amount, sourceWallet.getId());
            sourceWallet.setBalance(sourceWallet.getBalance() - amount);
            Double newSourceWalletBalance = sourceWallet.getBalance();
            log.info("Updated source wallet balance: {}",
                    newSourceWalletBalance);
            endTime = System.currentTimeMillis();
            log.info("Source wallet balance updated successfully"
                            +" for wallet {}",
                    endTime - startTime+"ms");
            
            if (sourceWallet.isInvested()) {
                log.info("Source wallet is an investment pot. "
                        + "Updating investment details.");
                Investment investment = investmentValidation
                        .getInvestmentBySubWalletId(sourceWallet.getId());
                
                 endTime = System.currentTimeMillis();
                log.info("Fetched investment for source wallet: {}",
                        endTime - startTime+"ms");
                
                if (!investment.isActive()) {
                    log.error("Attempted to debit money from an inactive "
                            + "investment.");
                    throw new InvestmentException(
                       "Cannot process payment from an inactive investment.");
                }
                PortfolioModel portfolioModel = investmentValidation
                      .getPortfolioModelById(investment.getPortfolioModelId());
                
                endTime = System.currentTimeMillis();
                log.info("Investment check for Unite Purchase Date {}"
                        , endTime - startTime+"ms");
                
                Units unit = investmentValidation
                        .findNextUnit(portfolioModel,
                                investment.getUnitPurchaseDate().toLocalDate());
                
                endTime = System.currentTimeMillis();
                log.info("Investment check for next unit {}",
                        endTime - startTime+"ms");
                
                log.info(
                       "Fetched unit for investment update: {}", unit);
                Investment updatedInvestment = investmentUtil
                        .updateInvestmentOnDebit(investment, unit, amount);

                investmentRepository.save(updatedInvestment);

                log.info("Investment details updated successfully:"
                        + investment.getInvestmentId());

            }
            endTime = System.currentTimeMillis();
            log.info("Internal transfer DB & validation finished at {}",
                    endTime - startTime+"ms");

            log.info("Creating debit transaction for sourceWallet={}",
                    sourceWallet.getId());

            Transaction debitTransaction = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .user(user)
                    .status("SUCCESS")
                    .isMaster(false)
                    .isInvestment(false)
                    .amount(amount)
                    .remainingBalance(newSourceWalletBalance)
                    .transactionType(TransactionType.DEBIT)
                    .transactionLevel(TransactionLevel.INTERNAL)
                    .description("Sent to " + targetWallet.getName())
                    .dateTime(LocalDateTime.now())
                    .fromWallet(sourceWallet.getName())
                    .fromWalletId(sourceWallet.getId())
                    .toWallet(targetWallet.getName())
                    .toWalletId(targetWallet.getId()).build();
            transactions.add(debitTransaction);
            log.info("Debit transaction created: {}",
                    debitTransaction.getTransactionId());

            log.info("Adding {} to target wallet {}",
                    amount, targetWallet.getId());
            
            endTime = System.currentTimeMillis();
            log.info("Target wallet balance before update : {} "
            , endTime - startTime+"ms");

            targetWallet.setBalance(targetWallet.getBalance() + amount);
            Double newTargetWalletBalance = targetWallet.getBalance();
            log.info("Updated target wallet balance: {}",
                    newTargetWalletBalance);

            if (targetWallet.isInvested()) {
                log.info("target wallet is an investment pot. "
                        + "Updating investment details.");
                Investment investment = investmentValidation
                        .getInvestmentBySubWalletId(targetWallet.getId());
                
                endTime = System.currentTimeMillis();
                log.info("Fetched investment for target wallet: {}",
                        endTime - startTime+"ms");

                if (!investment.isActive()) {
                    log.error(
                        "Attempted to add money to an inactive investment.");
                    throw new InvestmentException(
                        "Cannot process payment from an inactive investment.");
                }

                PortfolioModel portfolioModel = investmentValidation
                        .getPortfolioModelById(investment
                                .getPortfolioModelId());
                
                endTime = System.currentTimeMillis();
                log.info("Investment check for Unite Purchase Date {}"
                        , endTime - startTime+"ms");

                Units unit = investmentValidation
                        .findNextUnit(portfolioModel,
                                investment.getUnitPurchaseDate().toLocalDate());
                log.info(
                       "Fetched unit for investment update: {}", unit);
                
                endTime = System.currentTimeMillis();
                log.info("Updating investment with credited amount {}",
                        endTime - startTime+"ms");

                Investment updatedInvestment = investmentUtil.
                        updateInvestmentOnCredit(
                        investment, unit, amount);

                investmentRepository.save(updatedInvestment);

                log.info("Investment details updated successfully: {}",
                        investment.getInvestmentId());
                
                endTime = System.currentTimeMillis();
                log.info("Investment update on credit completed at {}",
                        endTime - startTime+"ms");

            }


            log.info("Creating credit transaction for targetWallet={}",
                    targetWallet.getId());
            Transaction creditTransaction = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .user(user)
                    .amount(amount)
                    .remainingBalance(newTargetWalletBalance)
                    .status("SUCCESS")
                    .isMaster(false)
                    .isInvestment(false)
                    .transactionType(TransactionType.CREDIT)
                    .transactionLevel(TransactionLevel.INTERNAL)
                    .description("Received from " + sourceWallet.getName())
                    .dateTime(LocalDateTime.now())
                    .fromWallet(sourceWallet.getName())
                    .fromWalletId(sourceWallet.getId())
                    .toWallet(targetWallet.getName())
                    .toWalletId(targetWallet.getId()).build();
            transactions.add(creditTransaction);
            log.info("Credit transaction created: {}",
                    creditTransaction.getTransactionId());
            
            endTime = System.currentTimeMillis();
            log.info("Saving Transactions to database {} "
            , endTime - startTime+"ms");

            log.debug("Saving transactions into repository, count={}",
                    transactions.size());
            transactionRepository.saveAll(transactions);
            log.info("Transactions saved successfully");
            
            endTime = System.currentTimeMillis();
            log.info("Updating main wallet with new transactions {}",
                    endTime - startTime+"ms");

            log.debug("Updating main wallet with new transactions");
            user.getTransactionHistory().addAll(transactions);
            mainWalletRepository.save(mainWallet);
            log.info("Main wallet updated successfully");
            
            endTime = System.currentTimeMillis();
            log.info("Main wallet updated with new transactions {}",
                    endTime - startTime+"ms");

            log.info("Publishing transaction event to Kafka topic "
                            + "'transaction-topic'");

            TransactionEvent transactionEvent = transactionMapper
                    .toTransactionEvent(creditTransaction);
            
            endTime = System.currentTimeMillis();
            log.info("After TransactionEvent mapping : {}",
                    endTime - startTime+"ms");

            kafkaTemplate.send("transaction-topic", transactionEvent);
            log.info("Transaction event published to Kafka successfully");


            log.info(
             "Internal transfer completed successfully for user {}",
                    user.getUuid());
            
            endTime = System.currentTimeMillis();
            log.info("Internal transfer process completed at {}",
                    endTime - startTime+"ms");
            return MainWalletResponse.builder()
                    .status("SUCCESS")
                    .sourceTransactionId(transactions.get(0).getTransactionId())
                    .targetTransactionId(transactions.get(1).getTransactionId())
                    .previousSourceWalletBalance(previousSourceWalletBalance)
                    .newSourceWalletBalance(newSourceWalletBalance)
                    .previousTargetWalletBalance(previousTargetWalletBalance)
                    .newTargetWalletBalance(newTargetWalletBalance)
                    .message("Transfer successful")
                    .build();
        } catch (Exception e) {
            log.error(
    "Internal transfer failed for user {}, amount {}, error: {}",
                    user.getUuid(), amount, e.getMessage(), e);
            throw e;
        }

    }
}

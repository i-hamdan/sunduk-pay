package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.exception.InvestmentException;
import com.bxb.sunduk_pay.factories.WalletFactory.WalletOperation;
import com.bxb.sunduk_pay.factories.WalletFactory.WalletOperationFactory;
import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import com.bxb.sunduk_pay.postgress.model.Units;
import com.bxb.sunduk_pay.repository.InvestmentRepository;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.repository.MasterWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.WalletService;
import com.bxb.sunduk_pay.util.InvestmentUtil;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.InvestmentValidation;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
//import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for wallet operations.
 * Such as adding money, paying money,
 * handling failed transactions, wallet CRUD operations,
 * and exporting transactions.
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {
    /**
     * Transaction type column index in Excel sheet.
     */
    private static final int TRANSACTION_TYPE_COLUMN = 1;
    /**
     * Amount column index in Excel sheet.
     */
    private static final int AMOUNT_COLUMN = 2;
    /**
     * Description column index in Excel sheet.
     */
    private static final int DESCRIPTION_COLUMN = 3;
    /**
     * Date column index in Excel sheet.
     */
    private static final int DATE_COLUMN = 4;

    /**
     * MasterWalletRepository for database operations on MasterWallets.
     */
    private final MasterWalletRepository masterWalletRepository;
    /**
     * MainWalletRepository for database operations on MainWallets.
     */
    private final MainWalletRepository mainWalletRepository;
    /**
     * TransactionRepository for database operations on Transactions.
     */
    private final TransactionRepository transactionRepository;
    /**
     * TransactionMapper for mapping Transaction entities to DTOs.
     */
    private final TransactionMapper transactionMapper;
    /**
     * KafkaTemplate for sending TransactionEvent messages to Kafka topics.
     */
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    /**
     * Wallet operation implementations based on request type.
     */
    private final WalletOperationFactory walletOperationFactory;
    /**
     * Validations for performing various validation checks.
     */
    private final Validations validations;
    /**
     * Investment validations for investment-related checks.
     */
    private final InvestmentValidation investmentValidation;
    /**
     * InvestmentRepository for database operations on Investments.
     */
    private final InvestmentRepository investmentRepository;
    /**
     * Utility class for investment-related operations.
     */
    private final InvestmentUtil investmentUtil;


    /**
     * Deducts money from the user's wallet(s) and records the transaction.
     *
     * @param request The request containing amount,
     *                source wallet, target wallet, and user UUID.
     * @return MainWalletResponse containing
     * transaction details and updated balances.
     */
    @Override
    @Transactional
    public MainWalletResponse payMoney(final MainWalletRequest request) {
        log.info("=== Deduct Money Request Started ===");
        log.debug("Request: {}", request);
        User user = validations.getUserInfo(request.getUuid());
        MasterWallet masterWallet = validations
                .getMasterWalletInfo(request.getUuid());
        MainWallet mainWallet = validations
                .getMainWalletInfo(request.getUuid());
        SubWallet sourcesubWallet = validations
                .findSubWalletIfExists(
                        mainWallet.getMainWalletId(),
                        request.getSourceWalletId());

        log.info("MasterWallet balance before: {}",
                masterWallet.getBalance());
        log.info("MainWallet balance before: {}",
                mainWallet.getBalance());

        Double previousSourceWalletBalance;
        if (sourcesubWallet != null) {
            log.info("Source SubWallet [{}] balance before: {}",
                    sourcesubWallet.getSubWalletName(),
                    sourcesubWallet.getBalance());
            previousSourceWalletBalance = sourcesubWallet.getBalance();
        } else {
            previousSourceWalletBalance = mainWallet.getBalance();
        }

        // deduct balance from master wallet
        masterWallet.setBalance(
                masterWallet.getBalance() - request.getAmount());
        log.info("Deducted {} from MasterWallet. New balance: {}",
                request.getAmount(),
                masterWallet.getBalance());

        List<Transaction> transactions = new ArrayList<>();
        Transaction masterWalletTxn = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .amount(request.getAmount())
                .transactionType(TransactionType.DEBIT)
                .transactionLevel(TransactionLevel.EXTERNAL)
                .paymentMethod(PaymentMethod.CARD)
                .status("SUCCESS")
                .description("Deducted from master wallet")
                .dateTime(LocalDateTime.now())
                .user(user)
                .fromWallet("Master Wallet")
                .fromWalletId(masterWallet.getMasterWalletId())
                .toWallet("Some external source")
                .toWalletId(UUID.randomUUID().toString())
                .isMaster(true)
                .isInvestment(false)
                .build();

        transactions.add(masterWalletTxn);

        Transaction debitTxn = null;
        if (sourcesubWallet != null) {
            if (Boolean.TRUE.equals(sourcesubWallet.getIsInvested())) {
                Investment investment = investmentValidation
             .getInvestmentBySubWalletId(sourcesubWallet.getSubWalletId());

                if (!investment.isActive()){
                    log.error("Attempted to process payment from an inactive investment.");
                    throw new InvestmentException(
  "Cannot process payment from an inactive investment.");
                }
                PortfolioModel portfolioModel = investmentValidation
                        .getPortfolioModelById(investment.getPortfolioModelId());

                Units unit = investmentValidation
                        .findNextUnit(portfolioModel,
                                investment.getUnitPurchaseDate().toLocalDate());
                Investment updatedInvestment = investmentUtil
                        .updateInvestmentOnDebit(investment, unit,
                        request.getAmount());


                investmentRepository.save(updatedInvestment);
                log.info("Updated investment [{}] after payment deduction.",
                        investment.getInvestmentId());
            }

            validations.validateBalance(sourcesubWallet.getBalance(),
                    request.getAmount());
            sourcesubWallet.setBalance(
                    sourcesubWallet.getBalance() - request.getAmount());
            Double newSourceWalletBalance = sourcesubWallet.getBalance();
            log.info(
                    "Deducted {} from SubWallet [{}]. New balance: {}",
                    request.getAmount(), sourcesubWallet.getSubWalletName(),
                    sourcesubWallet.getBalance());

            debitTxn = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .amount(request.getAmount())
                    .remainingBalance(newSourceWalletBalance)
                    .transactionType(TransactionType.DEBIT)
                    .transactionLevel(TransactionLevel.EXTERNAL)
                    .paymentMethod(PaymentMethod.CARD)
                    .isMaster(false)
                    .isInvestment(false)
                    .status("SUCCESS")
                    .description("Deducted from sub wallet")
                    .dateTime(LocalDateTime.now())
                    .user(user)
                    .fromWallet(sourcesubWallet.getSubWalletName())
                    .fromWalletId(sourcesubWallet.getSubWalletId())
                    .toWallet("some external source")
                    .toWalletId(UUID.randomUUID().toString()).build();
            transactions.add(debitTxn);
            TransactionEvent transactionEvent = transactionMapper
                    .toTransactionEvent(debitTxn);
            kafkaTemplate.send("transaction-topic",
                    transactionEvent);

        } else {
            validations.validateBalance(mainWallet.getBalance(),
                    request.getAmount());
            mainWallet.setBalance(
                    mainWallet.getBalance() - request.getAmount());
            log.info("Deducted {} from MainWallet. New balance: {}",
                    request.getAmount(),
                    mainWallet.getBalance());

            debitTxn = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .amount(request.getAmount())
                    .transactionType(TransactionType.DEBIT)
                    .transactionLevel(TransactionLevel.EXTERNAL)
                    .paymentMethod(PaymentMethod.CARD)
                    .isMaster(false)
                    .isInvestment(false)
                    .status("SUCCESS")
                    .description("Deducted from main wallet")
                    .dateTime(LocalDateTime.now())
                    .user(user)
                    .fromWallet("Main Wallet")
                    .fromWalletId(mainWallet.getMainWalletId())
                    .toWallet("some external target")
                    .toWalletId(UUID.randomUUID().toString()).build();
            transactions.add(debitTxn);

            TransactionEvent transactionEvent = transactionMapper
                    .toTransactionEvent(debitTxn);
            kafkaTemplate.send("transaction-topic", transactionEvent);
        }

        transactionRepository.saveAll(transactions);
        masterWalletRepository.save(masterWallet);
        user.getTransactionHistory().addAll(transactions);
        mainWalletRepository.save(mainWallet);

        MainWalletResponse response = MainWalletResponse.builder()
                .status("SUCCESS")
                .sourceTransactionId(
                        debitTxn.getTransactionId())
                .previousSourceWalletBalance(
                        previousSourceWalletBalance)
                .newSourceWalletBalance(
                        sourcesubWallet != null
                                ? sourcesubWallet.getBalance()
                                : mainWallet.getBalance()
                )
                .message("Transfer Successful")
                .build();

        log.info(" === Deduct Money Request Completed Successfully === ");
        log.debug("Response: {}", response);
        return response;
    }

    /**
     * Adds money to the user's wallet(s) and records the transaction.
     *
     * @param mainWalletRequest The request containing amount ,
     *                          source wallet ,
     *                          target wallet ,
     *                          and user UUID.
     * @return MainWalletResponse containing
     * transaction details and updated balances.
     */
    @Transactional
    @Override
    public MainWalletResponse addMoney(
            final MainWalletRequest mainWalletRequest) {
        log.info("=== Add Money Request Started ===");
        log.debug("Request: {}", mainWalletRequest);

        User user = validations
                .getUserInfo(mainWalletRequest.getUuid());
        MasterWallet masterWallet = validations
                .getMasterWalletInfo(mainWalletRequest.getUuid());
        MainWallet mainWallet = validations
                .getMainWalletInfo(mainWalletRequest.getUuid());
        SubWallet subWallet = validations
                .findSubWalletIfExists(
                        mainWallet.getMainWalletId(),
                        mainWalletRequest.getTargetWalletId());

        log.info("MasterWallet balance before: {}",
                masterWallet.getBalance());
        log.info("MainWallet balance before: {}",
                mainWallet.getBalance());

        Double previousTargetWalletBalance;
        if (subWallet != null) {
            log.info("Target SubWallet [{}] balance before: {}",
                    subWallet.getSubWalletName(),
                    subWallet.getBalance());
            previousTargetWalletBalance = subWallet.getBalance();
        } else {
            previousTargetWalletBalance = mainWallet.getBalance();
        }

        // adding amount on master wallet
        masterWallet.setBalance(masterWallet.getBalance()
                + mainWalletRequest.getAmount());
        log.info("Added {} to MasterWallet. New balance: {}",
                mainWalletRequest.getAmount(),
                masterWallet.getBalance());

        List<Transaction> transactions = new ArrayList<>();
        Transaction masterWalletTxn = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .amount(mainWalletRequest.getAmount())
                .user(user)
                .transactionType(TransactionType.CREDIT)
                .transactionLevel(TransactionLevel.EXTERNAL)
                .paymentMethod(PaymentMethod.CARD)
                .status("SUCCESS")
                .description("Credited to master wallet.")
                .dateTime(LocalDateTime.now())
                .fromWallet("Some external source.")
                .fromWalletId(UUID.randomUUID().toString())
                .toWallet("Master wallet")
                .toWalletId(masterWallet.getMasterWalletId())
                .isMaster(true)
                .isInvestment(false)
                .build();
        transactions.add(masterWalletTxn);

        Transaction creditTxn;
        Double newTargetWalletBalance = null;
        if (subWallet != null) {
            if (Boolean.TRUE.equals(subWallet.getIsInvested())) {

                Investment investment = investmentValidation
                        .getInvestmentBySubWalletId(subWallet.getSubWalletId());

                if (!investment.isActive()){
                    log.error("Attempted to add money to an inactive investment.");
                    throw new InvestmentException(
                            "Cannot process payment from an inactive investment.");
                }

                PortfolioModel portfolioModel = investmentValidation
                        .getPortfolioModelById(investment.getPortfolioModelId());

                Units unit = investmentValidation
                        .findNextUnit(portfolioModel,
                                investment.getUnitPurchaseDate().toLocalDate());

                Investment updatedInvestment = investmentUtil
                        .updateInvestmentOnCredit(investment, unit,
                        mainWalletRequest.getAmount());

                investmentRepository.save(updatedInvestment);
                log.info("Updated investment [{}] after adding money.",
                        investment.getInvestmentId());
            }

            subWallet.setBalance(subWallet.getBalance()
                    + mainWalletRequest.getAmount());
            newTargetWalletBalance = subWallet.getBalance();
            log.info("Added {} to SubWallet [{}]. New balance: {}",
                    mainWalletRequest.getAmount(),
                    subWallet.getSubWalletName(),
                    subWallet.getBalance());

            creditTxn = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .user(user)
                    .amount(mainWalletRequest.getAmount())
                    .remainingBalance(newTargetWalletBalance)
                    .transactionType(TransactionType.CREDIT)
                    .transactionLevel(TransactionLevel.EXTERNAL)
                    .paymentMethod(PaymentMethod.CARD)
                    .isMaster(false)
                    .isInvestment(false)
                    .status("SUCCESS")
                    .description("Credited to sub wallet : "
                            + subWallet.getSubWalletName())
                    .dateTime(LocalDateTime.now())
                    .fromWallet("Some external source.")
                    .fromWalletId(UUID.randomUUID().toString())
                    .toWallet(subWallet.getSubWalletName())
                    .toWalletId(subWallet.getSubWalletId()).build();

            transactions.add(creditTxn);
            TransactionEvent transactionEvent = transactionMapper
                    .toTransactionEvent(creditTxn);
            kafkaTemplate.send("transaction-topic",
                    transactionEvent);

        } else {
            mainWallet.setBalance(mainWallet.getBalance()
                    + mainWalletRequest.getAmount());
            newTargetWalletBalance = mainWallet.getBalance();
            log.info("Added {} to MainWallet. New balance: {}",
                    mainWalletRequest.getAmount(),
                    mainWallet.getBalance());

            creditTxn = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .user(user)
                    .amount(mainWalletRequest.getAmount())
                    .transactionType(TransactionType.CREDIT)
                    .transactionLevel(TransactionLevel.EXTERNAL)
                    .paymentMethod(PaymentMethod.CARD)
                    .isMaster(false)
                    .isInvestment(false)
                    .status("SUCCESS")
                    .description("Credited to main wallet.")
                    .dateTime(LocalDateTime.now())
                    .fromWallet("Some external source.")
                    .fromWalletId(UUID.randomUUID().toString())
                    .toWallet("Main wallet")
                    .toWalletId(mainWallet.getMainWalletId()).build();

            transactions.add(creditTxn);
            TransactionEvent transactionEvent = transactionMapper
                    .toTransactionEvent(creditTxn);
            log.info("sending transaction event to kafka: {}",
                    transactionEvent);
            kafkaTemplate.send("transaction-topic",
                    transactionEvent);

        }

        transactionRepository.saveAll(transactions);
        user.getTransactionHistory()
                .addAll(transactions);
        masterWalletRepository.save(masterWallet);
        mainWalletRepository.save(mainWallet);

        MainWalletResponse response = MainWalletResponse
                .builder()
                .status("SUCCESS")
                .targetTransactionId(transactions.get(1)
                        .getTransactionId())
                .previousTargetWalletBalance(previousTargetWalletBalance)
                .newTargetWalletBalance(newTargetWalletBalance)
                .message("amount recived successfull")
                .build();

        log.info(" === Add Money Request Completed Successfully === ");
        return response;
    }

    /**
     * Provides wallet-related operations such as CRUD operations.
     * And recording failed transactions,
     * adding dummy transactions, checking balances,
     * and downloading transaction history.
     */

    @Override
    public MainWalletResponse walletCrud(
            final MainWalletRequest mainWalletRequest) {
        WalletOperation walletService = walletOperationFactory.
                getWalletService(mainWalletRequest.getRequestType());
        return walletService.perform(mainWalletRequest);
    }

    /**
     * Records a dummy transaction in the database.
     *
     * @param request The request containing
     *                transaction details such as source ,
     *                target , amount , and type.
     */
    @Override
    public void addDummy(final MainWalletRequest request) {
        User user = validations.getUserInfo(request.getUuid());
        MainWallet mainWallet = validations
                .getMainWalletInfo(user.getUuid());

        Transaction txn = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .user(user)
                .amount(request.getAmount())
                .transactionLevel(TransactionLevel.EXTERNAL)
                .transactionType(request.getTransactionType())
                .paymentMethod(request.getPaymentMethod())
                .dateTime(LocalDateTime.now())
                .isMaster(false)
                .isInvestment(false)
                .toWallet("Dummy Wallet")
                .toWalletId(UUID.randomUUID().toString())
                .fromWallet("Dummy Wallet")
                .fromWalletId(UUID.randomUUID().toString())
                .build();
        transactionRepository.save(txn);
    }

}

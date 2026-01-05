package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.exception.InsufficientBalanceException;
import com.bxb.sunduk_pay.exception.WalletNotFoundException;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.*;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * {@code AddContributerService} handles the business logic
 * for adding a contributor to a Global Pot.
 *
 * <p>This service performs the following operations:</p>
 * <ul>
 *     <li>Fetches user and GlobalPot information</li>
 *     <li>Validates sufficient balance in wallets</li>
 *     <li>Deducts amount from Master/Main/Sub wallet</li>
 *     <li>Credits amount to GlobalWallet</li>
 *     <li>Creates and stores transaction records</li>
 *     <li>Updates contributed balance of GlobalPot</li>
 * </ul>
 *
 * <p>This service is transactional, ensuring atomicity
 * of all wallet and transaction operations.</p>
 */
@Service
@Slf4j
public class AddContributerService implements GlobalPotOperation {

    private final ContributerRepository contributerRepository;
    private final GlobalPotValidations globalPotValidations;
    private final GlobalPotMapper globalPotMapper;
    private final Validations validations;
    private final MainWalletRepository mainWalletRepository;
    private final MasterWalletRepository masterWalletRepository;
    private final GlobalWalletRepository globalWalletRepository;
    private final TransactionRepository transactionRepository;
    private final SubWalletRepository subWalletRepository;
    private final GlobalPotRepository globalPotRepository;

    public AddContributerService(
            ContributerRepository contributerRepository,
            GlobalPotValidations globalPotValidations,
            GlobalPotMapper globalPotMapper,
            Validations validations,
            MainWalletRepository mainWalletRepository,
            MasterWalletRepository masterWalletRepository,
            GlobalWalletRepository globalWalletRepository,
            TransactionRepository transactionRepository,
            SubWalletRepository subWalletRepository,
            GlobalPotRepository globalPotRepository
    ) {
        this.contributerRepository = contributerRepository;
        this.globalPotValidations = globalPotValidations;
        this.globalPotMapper = globalPotMapper;
        this.validations = validations;
        this.mainWalletRepository = mainWalletRepository;
        this.masterWalletRepository = masterWalletRepository;
        this.transactionRepository = transactionRepository;
        this.globalWalletRepository = globalWalletRepository;
        this.subWalletRepository = subWalletRepository;
        this.globalPotRepository = globalPotRepository;
    }

    /**
     * Returns the {@link GlobalPotRequestType} handled by this service.
     *
     * @return {@link GlobalPotRequestType#ADD_CONTRIBUTOR}
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.ADD_CONTRIBUTOR;
    }

    /**
     * Adds a contributor to a Global Pot by transferring the specified amount
     * from the user's wallet to the Global Pot wallet.
     *
     * <p>The method performs validation, wallet balance deduction,
     * transaction creation, and persistence of all related entities.</p>
     *
     * @param request {@link GlobalPotRequest} containing contributor details,
     *                wallet information, and contribution amount
     * @return {@link GlobalPotResponse} with success message
     * @throws IOException                  if any I/O related issue occurs
     * @throws InsufficientBalanceException if wallet balance is insufficient
     */
    @Override
    @Transactional
    public GlobalPotResponse perform(GlobalPotRequest request) throws IOException {

        log.info("AddContributor started | userId={} globalPotId={} amount={}",
                request.getUserContributorId(),
                request.getGlobalPotId(),
                request.getAmountContributed());

        User user = validations.getUserInfo(request.getUserContributorId());
        log.info("User fetched successfully | userId={}", user.getUuid());

        GlobalPot globalPot =
                globalPotValidations.getGlobalPot(request.getGlobalPotId());
        log.info("GlobalPot fetched | globalPotId={}", globalPot.getGlobalPotId());


        MasterWallet masterWallet = masterWalletRepository
                .findByUserUuid(request.getUserContributorId())
                .orElseThrow(() -> new WalletNotFoundException("MasterWallet not found"));

        log.info("MasterWallet balance before deduction={}",
                masterWallet.getBalance());

        validations.validateBalance(
                masterWallet.getBalance(),
                request.getAmountContributed()
        );

        masterWallet.setBalance(
                masterWallet.getBalance() - request.getAmountContributed()
        );

        log.info("MasterWallet balance after deduction={}",
                masterWallet.getBalance());

        List<Transaction> transactions = new ArrayList<>();

        Transaction masterWalletTxn = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .isMaster(true)
                .isInvestment(false)
                .transactionLevel(TransactionLevel.GLOBAL_POT)
                .amount(request.getAmountContributed())
                .transactionType(TransactionType.DEBIT)
                .status("SUCCESS")
                .description("Deducted from master wallet")
                .dateTime(LocalDateTime.now())
                .user(user)
                .fromWallet("Master Wallet")
                .fromWalletId(request.getSourceWalletId())
                .build();

        transactions.add(masterWalletTxn);

        GlobalWallet globalWallet =
                globalWalletRepository.findById(
                        globalPot.getGlobalWallet().getGlobalWalletId()
                ).orElseThrow(() -> new WalletNotFoundException("GlobalWallet not found"));

        log.info("GlobalWallet balance before credit={}",
                globalWallet.getBalance());

        globalWallet.setBalance(
                globalWallet.getBalance() + request.getAmountContributed()
        );

        log.info("GlobalWallet balance after credit={}",
                globalWallet.getBalance());

        MainWallet mainWallet =
                mainWalletRepository.findByUserUuid(request.getUserContributorId())
                        .orElseThrow(() ->
                                new WalletNotFoundException("Main wallet not found"));

        String mainWalletId = mainWallet.getMainWalletId();

        SubWallet subWallet =
                validations.findSubWalletIfExists(
                        mainWalletId,
                        request.getSourceWalletId()
                );

        if (subWallet != null) {

            log.info("SubWallet found | subWalletId={}",
                    subWallet.getSubWalletId());

            log.info("SubWallet balance before deduction={}",
                    subWallet.getBalance());

            validations.validateBalance(
                    subWallet.getBalance(),
                    request.getAmountContributed()
            );

            subWallet.setBalance(
                    subWallet.getBalance() - request.getAmountContributed()
            );

            log.info("SubWallet balance after deduction={}",
                    subWallet.getBalance());

            Transaction subWalletTxn = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .isMaster(false)
                    .isInvestment(false)
                    .transactionLevel(TransactionLevel.GLOBAL_POT)
                    .amount(request.getAmountContributed())
                    .transactionType(TransactionType.DEBIT)
                    .status("SUCCESS")
                    .description("Deducted from sub wallet")
                    .dateTime(LocalDateTime.now())
                    .user(user)
                    .fromWallet("Sub Wallet")
                    .fromWalletId(request.getSourceWalletId())
                    .build();

            transactions.add(subWalletTxn);
            subWalletRepository.save(subWallet);

        } else {

            log.info("No SubWallet found, using MainWallet | mainWalletId={}",
                    mainWalletId);

            log.info("MainWallet balance before deduction={}",
                    mainWallet.getBalance());

            validations.validateBalance(
                    mainWallet.getBalance(),
                    request.getAmountContributed()
            );

            mainWallet.setBalance(
                    mainWallet.getBalance() - request.getAmountContributed()
            );

            log.info("MainWallet balance after deduction={}",
                    mainWallet.getBalance());

            Transaction mainWalletTxn = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .isMaster(false)
                    .isInvestment(false)
                    .transactionLevel(TransactionLevel.GLOBAL_POT)
                    .amount(request.getAmountContributed())
                    .transactionType(TransactionType.DEBIT)
                    .status("SUCCESS")
                    .description("Deducted from main wallet")
                    .dateTime(LocalDateTime.now())
                    .user(user)
                    .fromWallet("Main Wallet")
                    .fromWalletId(request.getSourceWalletId())
                    .build();

            transactions.add(mainWalletTxn);
            mainWalletRepository.save(mainWallet);
        }

        log.info("GlobalPot contributedBalance before update={}",
                globalPot.getContributedBalance());

        globalPot.setContributedBalance(
                globalPot.getContributedBalance() + request.getAmountContributed()
        );

        log.info("GlobalPot contributedBalance after update={}",
                globalPot.getContributedBalance());

        Contributor contributor =
                globalPotMapper.toContributerEntity(request, globalPot);

        masterWalletRepository.save(masterWallet);
        transactionRepository.saveAll(transactions);
        globalWalletRepository.save(globalWallet);
        globalPotRepository.save(globalPot);
        contributerRepository.save(contributor);

        log.info("Contributor added successfully | userId={} globalPotId={} amount={}",
                request.getUserContributorId(),
                request.getGlobalPotId(),
                request.getAmountContributed());

        return GlobalPotResponse.builder()
                .message("Contributor added successfully")
                .build();
    }
}

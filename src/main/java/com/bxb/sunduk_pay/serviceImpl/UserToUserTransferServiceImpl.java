package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.exception.InvestmentException;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.MasterWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import com.bxb.sunduk_pay.postgress.model.Units;
import com.bxb.sunduk_pay.repository.InvestmentRepository;
import com.bxb.sunduk_pay.repository.ReminderRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.service.UserToUserTransferService;
import com.bxb.sunduk_pay.util.GenerateKeyUtil;
import com.bxb.sunduk_pay.util.InvestmentUtil;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.validations.InvestmentValidation;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for handling user-to-user fund transfers.
 * This service manages the entire transfer process, including balance validation,
 * transaction recording, and updating wallet balances for both sender and receiver.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class UserToUserTransferServiceImpl
        implements UserToUserTransferService {

    /**
     * Transaction expiry time in hours for Redis storage.
     */
    private static final int TRANSACTION_EXPIRY_HOURS = 24;
    /**
     * Validations utility for business rule enforcement.
     */
    private final Validations validations;

    /**
     * TransactionRepository for database operations on Transactions.
     */
    private final TransactionRepository transactionRepository;

    /**
     * Redis template for saving transactions in redis.
     */
    private final RedisTemplate<String, TransactionResponse> redisTemplate;
    /**
     * TransactionMapper for converting Transaction entities to DTOs.
     */
    private final TransactionMapper transactionMapper;

    /**
     * Util for generating redis key.
     */
    private final GenerateKeyUtil generateKeyUtil;

    /**
     * InvestmentRepository for database operations on Investments.
     */
    private final InvestmentRepository investmentRepository;

    /**
     * InvestmentValidation for investment-related validations.
     */
    private final InvestmentValidation investmentValidation;

    /**
     * InvestmentUtil for investment-related utilities.
     */
    private final InvestmentUtil investmentUtil;
    /**
     * Reminder for Fetch the Reminder.
     */
    private final ReminderRepository reminderRepository;

    /**
     * Transfers funds between two users wallets.
     *
     * @param senderId       the UUID of the sender user
     * @param receiverId     the phone number of the receiver user
     * @param amount         the amount to transfer
     * @param senderWalletId the ID of the sender's wallet
     * @return MainWalletResponse containing transfer details
     */
    @Transactional
    @Override
    public MainWalletResponse transferBetweenUsers(
            final String senderId,
            final String receiverId,
            final Double amount,
            final String paymentTag,
            final String senderWalletId,
            final String reminderId,
            Boolean isAutoPayment
    ) {

        User user = validations.getUserInfo(senderId);
log.info(
"Initiating transfer of amount "+amount+" from user "+senderId
        + " to receiver with phone number "+receiverId
                );

        MainWallet senderMainWallet = validations.getMainWalletInfo(senderId);
        log.info("Fetched sender's main wallet for user "+senderId);

        MasterWallet senderMasterWallet = validations
                .getMasterWalletInfo(senderId);
log.info("Fetched sender's master wallet for user "+senderId);

        SubWallet subWallet = validations.findSubWalletIfExists(
                senderMainWallet.getMainWalletId(),
                senderWalletId
        );
        log.info("Fetched sender's sub wallet if exists "
                + "for wallet ID "+senderWalletId);

        User userByPhoneNumber = validations.getUserByPhoneNumber(receiverId);
log.info("Fetched receiver user by phone number "+receiverId);

        MasterWallet receiverMasterWallet =
                validations.getMasterWalletInfo(userByPhoneNumber.getUuid());
log.info("Fetched receiver's master wallet for user "
        +userByPhoneNumber.getUuid());

        MainWallet receiverMainWallet =
                validations.getMainWalletInfo(userByPhoneNumber.getUuid());
        log.info("Fetched receiver's main wallet for user "
                +userByPhoneNumber.getUuid());

        String key = generateKeyUtil.generateTransactionKey(
                user.getUuid(),
                userByPhoneNumber.getUuid()
        );
        log.info("Generated Redis key for transaction: "+key);

        List<Transaction> transactions = new ArrayList<>();

        // ===== DETERMINE SOURCE WALLET DETAILS =====
        String sourceWalletId;
        String sourceWalletName;
        Double sourceBalance;

        if (subWallet != null) {
            sourceWalletId = subWallet.getSubWalletId();
            sourceWalletName = subWallet.getSubWalletName();
            sourceBalance = subWallet.getBalance();
        } else {
            sourceWalletId = senderMainWallet.getMainWalletId();
            sourceWalletName = "Main Wallet";
            sourceBalance = senderMainWallet.getBalance();
        }

        // ===== VALIDATE BALANCE =====
        validations.validateBalance(sourceBalance, amount);
        log.info("Balance validation successful for user "+senderId
                + " with source wallet "+sourceWalletName
                + " and amount "+amount);

        // ===== DEBIT MASTER WALLET =====
        senderMasterWallet.setBalance(
                senderMasterWallet.getBalance() - amount);
        log.info("Debited sender's master wallet for user " + senderId
                + " by amount " + amount);

        // ===== DEBIT SOURCE WALLET =====
        if (subWallet != null) {
log.info("Debiting sender's sub wallet for user " + senderId
        + " by amount " + amount);

            subWallet.setBalance(subWallet.getBalance() - amount);
log.info("Debited sender's sub wallet for user " + senderId
        + " by amount " + amount);

            if (Boolean.TRUE.equals(subWallet.getIsInvested())) {
log.info("Sub wallet is invested. Processing"
        + " investment debit for user " + senderId);

                Investment investment = investmentValidation
                        .getInvestmentBySubWalletId(subWallet.getSubWalletId());

                if (!investment.isActive()) {
                    throw new InvestmentException(
                            "Cannot process payment from an inactive investment.");
                }

                PortfolioModel portfolioModel =
                        investmentValidation.getPortfolioModelById(
                                investment.getPortfolioModelId());

                Units unit = investmentValidation.findNextUnit(
                        portfolioModel,
                        investment.getUnitPurchaseDate().toLocalDate());

                Investment updatedInvestment =
                        investmentUtil.updateInvestmentOnDebit(
                                investment, unit, amount);

                investmentRepository.save(updatedInvestment);
            }

        } else {
            log.info("Debiting sender's main wallet for user " + senderId
                    + " by amount " + amount);
            senderMainWallet.setBalance(senderMainWallet.getBalance() - amount);
        }

        // ===== MARK REMINDER PAID =====
        if (reminderId != null) {
            log.info("Marking reminder as paid for reminder ID " + reminderId
                    + " for user " + senderId);
            Reminder reminder = validations.getReminderById(reminderId);
            reminder.setIsPaid(true);
            reminder.setLocalDateTime(null);
            reminderRepository.save(reminder);
        }

        String descriptionPrefix = Boolean.TRUE.equals(isAutoPayment)
                ? "Auto Payment to "
                : "Sent to ";
        log.info("Set transaction description prefix: " + descriptionPrefix);

        String receivedPrefix = Boolean.TRUE.equals(isAutoPayment)
                ? "Auto Payment received from "
                : "Received from ";
log.info("Set transaction description prefix for receiver: " + receivedPrefix);

        // ===== MASTER WALLET DEBIT TRANSACTION =====
        Transaction sourceMasterDebitTxn = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .user(user)
                .status("SUCCESS")
                .isMaster(true)
                .isInvestment(false)
                .amount(amount)
                .transactionType(TransactionType.DEBIT)
                .transactionLevel(TransactionLevel.EXTERNAL)
                .paymentMethod(PaymentMethod.PHONE_NUMBER)
                .description(
                        descriptionPrefix + userByPhoneNumber.getFullName())
                .dateTime(LocalDateTime.now())
                .paymentTag(paymentTag)
                .fromWallet("Master Wallet")
                .fromWalletId(senderMasterWallet.getMasterWalletId())
                .fromPhoneNumber(user.getPhoneNumber())
                .toWallet(userByPhoneNumber.getFullName())
                .toWalletId(receiverMainWallet.getMainWalletId())
                .toPhoneNumber(userByPhoneNumber.getPhoneNumber())
                .build();

        transactions.add(sourceMasterDebitTxn);

        // ===== SOURCE WALLET DEBIT TRANSACTION =====
        Transaction sourceDebitTxn = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .user(user)
                .status("SUCCESS")
                .isMaster(false)
                .isInvestment(false)
                .amount(amount)
                .transactionType(TransactionType.DEBIT)
                .transactionLevel(TransactionLevel.EXTERNAL)
                .paymentMethod(PaymentMethod.PHONE_NUMBER)
                .description(
                        descriptionPrefix + userByPhoneNumber.getFullName())
                .dateTime(LocalDateTime.now())
                .paymentTag(paymentTag)
                .fromWallet(sourceWalletName)
                .fromWalletId(sourceWalletId)
                .fromPhoneNumber(user.getPhoneNumber())
                .toWallet(userByPhoneNumber.getFullName())
                .toWalletId(receiverMainWallet.getMainWalletId())
                .toPhoneNumber(userByPhoneNumber.getPhoneNumber())
                .build();

        transactions.add(sourceDebitTxn);

        redisTemplate.opsForList().rightPush(
                key,
                transactionMapper.toTransactionResponse(sourceDebitTxn)
        );

        // ===== CREDIT RECEIVER =====
        receiverMasterWallet.setBalance(
                receiverMasterWallet.getBalance() + amount);

        receiverMainWallet.setBalance(
                receiverMainWallet.getBalance() + amount);

        // ===== RECEIVER MASTER CREDIT TXN =====
        Transaction targetMasterCreditTxn = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .user(userByPhoneNumber)
                .status("SUCCESS")
                .isMaster(true)
                .isInvestment(false)
                .amount(amount)
                .transactionType(TransactionType.CREDIT)
                .transactionLevel(TransactionLevel.EXTERNAL)
                .paymentMethod(PaymentMethod.PHONE_NUMBER)
                .description(receivedPrefix + user.getFullName())
                .dateTime(LocalDateTime.now())
                .paymentTag(paymentTag)
                .fromWallet(user.getFullName())
                .fromWalletId(sourceWalletId)
                .fromPhoneNumber(user.getPhoneNumber())
                .toWallet("Master Wallet")
                .toWalletId(receiverMasterWallet.getMasterWalletId())
                .toPhoneNumber(userByPhoneNumber.getPhoneNumber())
                .build();

        transactions.add(targetMasterCreditTxn);

        // ===== RECEIVER MAIN CREDIT TXN =====
        Transaction targetCreditTxn = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .user(userByPhoneNumber)
                .status("SUCCESS")
                .isMaster(false)
                .isInvestment(false)
                .amount(amount)
                .transactionType(TransactionType.CREDIT)
                .transactionLevel(TransactionLevel.EXTERNAL)
                .paymentMethod(PaymentMethod.PHONE_NUMBER)
                .description(receivedPrefix + user.getFullName())
                .dateTime(LocalDateTime.now())
                .paymentTag(paymentTag)
                .fromWallet(user.getFullName())
                .fromWalletId(sourceWalletId)
                .fromPhoneNumber(user.getPhoneNumber())
                .toWallet("Main wallet")
                .toWalletId(receiverMainWallet.getMainWalletId())
                .toPhoneNumber(userByPhoneNumber.getPhoneNumber())
                .build();

        redisTemplate.opsForList().rightPush(
                key,
                transactionMapper.toTransactionResponse(targetCreditTxn));

        transactions.add(targetCreditTxn);

        transactionRepository.saveAll(transactions);

        redisTemplate.expire(key, Duration.ofHours(TRANSACTION_EXPIRY_HOURS));

        return MainWalletResponse.builder()
                .message("Transfer Successful")
                .transactionHistory(transactionMapper
                        .toTransactionsResponse(List.of(sourceDebitTxn)))
                .build();
    }

}

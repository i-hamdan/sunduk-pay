package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.repository.MasterWalletRepository;
import com.bxb.sunduk_pay.repository.ReminderRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.ExternalTransferService;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service implementation for handling external transfers
 * such as UPI payments.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class ExternalTransferServiceImpl implements ExternalTransferService {

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
     * Validations utility for business rule enforcement.
     */
    private final Validations validations;
    /**
     * Reminder for SAVE isPad and IsActive .
     */
    private final ReminderRepository reminderRepository;

    /**
     * Handles UPI transfer requests.
     *
     * @param request the main wallet request containing transfer details
     * @return the response containing updated wallet information
     */
    @Override
    public MainWalletResponse handleUPITransfer(final MainWalletRequest request)
    {
        log.info("Initiating UPI transfer for UUID: {}, Amount: {}, " +
                "RecipientUPI: {}", request.getUuid(), request.getAmount(),
                request.getRecipientUpiId());

        try {
            // Step 1: Validate recipient UPI ID first
            validations.validateRecipientUpiId(request.getRecipientUpiId());

            // Step 2: Validate and retrieve user, main, and master wallets
            User user = validations.getUserInfo(request.getUuid());
            log.debug("Fetched user details: {}", user.getUuid());

            MainWallet mainWallet =
                    validations.getMainWalletInfo(request.getUuid());
            log.debug("Fetched main wallet: {}",
                    mainWallet.getMainWalletId());


            MasterWallet masterWallet =
                    validations.getMasterWalletInfo(request.getUuid());
            log.debug("Fetched master wallet: {}",
                    masterWallet.getMasterWalletId());

            // Step 3: Validate balance
            log.info("Validating main wallet balance:" +
                            " {} for amount: {}",
                    mainWallet.getBalance(), request.getAmount());
            validations.validateBalance(mainWallet.getBalance(),
                    request.getAmount());

            // Step 4: Deduct balances
            log.info("Deducting amount from master and main wallets...");
            masterWallet.setBalance(masterWallet.getBalance() -
                    request.getAmount());
            mainWallet.setBalance(mainWallet.getBalance() -
                    request.getAmount());

            if(request.getReminderId() != null){
                Reminder reminder = validations.getReminderById(request.getReminderId());
                reminder.setLocalDateTime(null);
                reminder.setIsPaid(true);
                reminderRepository.save(reminder);
            }

            // Step 5: Create transactions
            log.info("Creating transaction records for UPI transfer...");

            List<Transaction> transactions = new ArrayList<>();

            Transaction masterWalletTxn =
                    Transaction.builder()
                            .transactionId(UUID.randomUUID().toString())
                            .user(user)
                            .amount(request.getAmount())
                            .recipientUpiId(request.getRecipientUpiId())
                            .paymentMethod(PaymentMethod.PHONE_NUMBER)
                            .transactionType(TransactionType.DEBIT)
                            .transactionLevel(TransactionLevel.EXTERNAL)
                            .dateTime(LocalDateTime.now())
                            .status("SUCCESS")
                            .description("Deducted from Master Wallet for" +
                                    " UPI transfer").fromWallet("Master Wallet")
                            .fromWalletId(masterWallet.getMasterWalletId())
                            .isInvestment(false)
                            .isMaster(true).build();

            Transaction mainWalletTxn =
                    Transaction.builder().user(user).amount(request.getAmount())
                            .transactionId(UUID.randomUUID().toString())
                            .recipientUpiId(request.getRecipientUpiId())
                            .paymentMethod(PaymentMethod.PHONE_NUMBER)
                            .transactionType(TransactionType.DEBIT)
                            .transactionLevel(TransactionLevel.EXTERNAL)
                            .dateTime(LocalDateTime.now())
                            .status("SUCCESS")
                            .paymentTag(request.getPaymentTag())
                            .description("Deducted from Main Wallet for" +
                                    " UPI transfer").fromWallet("Main Wallet")
                            .fromWalletId(mainWallet.getMainWalletId())
                            .isInvestment(false)
                            .isMaster(false).build();

            transactions.add(masterWalletTxn);
            transactions.add(mainWalletTxn);

            // Step 6: Save updates to DB
            log.info("Saving transactions and updating wallet balances...");
            transactionRepository.saveAll(transactions);
            masterWalletRepository.save(masterWallet);
            mainWalletRepository.save(mainWallet);

            log.info("UPI transfer completed successfully " +
                            "for UUID: " + "{}",
                    user.getUuid());

            // Step 7: Build response
            return MainWalletResponse.builder()
                    .transferredAmount(request.getAmount())
                    .transactionDate(LocalDateTime.now()).build();

        } catch (Exception e) {
            log.error("Error occurred during UPI transfer" +
                    " for UUID: " + "{}. "
                    + "Message: {}", request.getUuid(), e.getMessage(), e);
            throw e; // rethrow to be handled by global exception handler
        }
    }
}

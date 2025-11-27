package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.exception.WalletNotFoundException;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.MasterWallet;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.service.UserToUserTransferService;
import com.bxb.sunduk_pay.util.GenerateKeyUtil;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.Validations;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserToUserTransferServiceImpl
        implements UserToUserTransferService {

    /**
     * Validations utility for business rule enforcement.
     */
    private final Validations validations;

    /**
     * TransactionRepository for database operations on Transactions.
     */
    private final TransactionRepository transactionRepository;

    /**
     * Redis template for saving transactions in redis
     */
    private final RedisTemplate<String, TransactionResponse> redisTemplate;
    /**
     * TransactionMapper for converting Transaction entities to DTOs.
     */
    private final TransactionMapper transactionMapper;

    /**
     * Util for generating redis key
     */
    private final GenerateKeyUtil generateKeyUtil;


    /**
     * Transfers funds between two users wallets.
     *
     * @param senderId       the UUID of the sender user
     * @param receiverId     the phone number of the receiver user
     * @param amount         the amount to transfer
     * @param senderWalletId the ID of the sender's wallet
     * @return MainWalletResponse containing transfer details
     */
    @Override
    public MainWalletResponse transferBetweenUsers(
            final String senderId,
            final String receiverId,
            final Double amount,
            final String paymentTag,
            final String senderWalletId) {

        User user = validations.getUserInfo(senderId);
        MainWallet senderMainWallet = validations
                .getMainWalletInfo(senderId);

        MasterWallet senderMasterWallet = validations
                .getMasterWalletInfo(senderId);

        WalletWrapper senderWallet = getWallet(
                senderMainWallet, senderWalletId);

        User userByPhoneNumber = validations.getUserByPhoneNumber(receiverId);
        MasterWallet receiverMasterWallet = validations
                .getMasterWalletInfo(userByPhoneNumber.getUuid());
        MainWallet receiverMainWallet = validations
                .getMainWalletInfo(userByPhoneNumber.getUuid());

        String key = generateKeyUtil.generateTransactionKey(user.getUuid(),
                userByPhoneNumber.getUuid());


        log.info("Validating sufficient balance in source wallet.");
        validations.validateBalance(senderWallet.getBalance(), amount);
        log.info("Sufficient balance validated. Proceeding with transfer.");

        log.info("deducting amount from source wallet");
  senderMasterWallet.setBalance(senderMasterWallet.getBalance() - amount);
        senderWallet.setBalance(senderWallet.getBalance() - amount);

        log.info("adding amount to target wallet");
        receiverMasterWallet.setBalance(
                receiverMasterWallet.getBalance() + amount);
        receiverMainWallet.setBalance(
                receiverMainWallet.getBalance() + amount);

        List<Transaction> transactions = new ArrayList<>();

        log.info(
            "Creating debit transaction for sourceMasterWallet={}",
                senderMasterWallet.getMasterWalletId());
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
                .description("Sent to " + userByPhoneNumber.getFullName())
                .dateTime(LocalDateTime.now())
                .paymentTag(paymentTag)
                .fromWallet("Master Wallet")
                .fromWalletId(senderMasterWallet.getMasterWalletId())
                .fromPhoneNumber(user.getPhoneNumber())
                .toWallet(userByPhoneNumber.getFullName())
                .toWalletId(receiverMainWallet.getMainWalletId())
                .toPhoneNumber(userByPhoneNumber.getPhoneNumber()).build();
        transactions.add(sourceMasterDebitTxn);

        log.info("Creating debit transaction for sourceWallet={}",
                senderWallet.getId());
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
                .description("Sent to " + userByPhoneNumber.getFullName())
                .dateTime(LocalDateTime.now())
                .paymentTag(paymentTag)
                .fromWallet(senderWallet.getName())
                .fromWalletId(senderWallet.getId())
                .fromPhoneNumber(user.getPhoneNumber())
                .toWallet(userByPhoneNumber.getFullName())
                .toWalletId(receiverMainWallet.getMainWalletId())
                .toPhoneNumber(userByPhoneNumber.getPhoneNumber()).build();
        transactions.add(sourceDebitTxn);
        redisTemplate.opsForList().rightPush(key,
                transactionMapper.toTransactionResponse(sourceDebitTxn));

        log.info(
          "Creating credit transaction for targetMasterWallet={}",
                receiverMasterWallet.getMasterWalletId());
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
                .description("received from" + user.getFullName())
                .dateTime(LocalDateTime.now())
                .paymentTag(paymentTag)
                .fromWallet(user.getFullName())
                .fromWalletId(senderWallet.getId())
                .fromPhoneNumber(user.getPhoneNumber())
                .toWallet("Master Wallet")
                .toWalletId(receiverMasterWallet.getMasterWalletId())
                .toPhoneNumber(userByPhoneNumber.getPhoneNumber()).build();
        transactions.add(targetMasterCreditTxn);

        log.info("Creating credit transaction for targetWallet={}",
                receiverMainWallet.getMainWalletId());
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
                .description("Received from " + user.getFullName())
                .dateTime(LocalDateTime.now())
                .paymentTag(paymentTag)
                .fromWallet(user.getFullName())
                .fromWalletId(senderWallet.getId())
                .fromPhoneNumber(user.getPhoneNumber())
                .toWallet("Main wallet")
                .toWalletId(receiverMainWallet.getMainWalletId())
                .toPhoneNumber(userByPhoneNumber.getPhoneNumber()).build();
                 redisTemplate.opsForList().rightPush(key,
                transactionMapper.toTransactionResponse(targetCreditTxn));
        transactions.add(targetCreditTxn);

        log.info("Saving all transactions to the database.");
        transactionRepository.saveAll(transactions);

        redisTemplate.expire(key, Duration.ofHours(24));
        log.info("All transactions saved successfully. Transfer complete.");

        return MainWalletResponse.builder()
                .message("Transfer Successful")
                .transactionHistory(transactionMapper.toTransactionsResponse(
                        List.of(sourceDebitTxn))).build();
    }

    /**
     * Get WalletWrapper for mainWallet or subWallet based on walletId.
     *
     * @param mainWallet the main wallet containing sub-wallets,
     * @param walletId   the ID of the wallet to retrieve
     * @return WalletWrapper for the specified walletId,
     * or null if not found
     */
    private WalletWrapper getWallet(
            final MainWallet mainWallet,
            final String walletId) {

        if (walletId == null) {
            log.warn("walletId is null, returning null");
            return null;
        }

        if (walletId.equals(mainWallet.getMainWalletId())) {
            log.debug("Returning main wallet wrapper for wallet ID {}",
                    walletId);
            return new WalletWrapper(mainWallet);
        }
        log.debug("Requested wallet ID {} does not match MainWallet. "
                        + "Validating sub wallet.",
                walletId);


        SubWallet subWallet = validations.findSubWalletIfExists(
                mainWallet.getMainWalletId(),
                walletId);
        log.debug("Returning sub wallet wrapper for wallet ID {}",
                walletId);
        if (subWallet != null) {
            log.debug("SubWallet found for wallet ID {}."
                            + " Returning SubWallet wrapper.",
                    walletId);
            return new WalletWrapper(subWallet);
        } else {
            log.error(
      "No Wallet found for wallet ID {} in MainWalletId {}.",
                    walletId, mainWallet.getMainWalletId());
            throw new WalletNotFoundException("Wallet with id "+ walletId +" "
                    + "does not belong to to user : "+mainWallet.
                    getUser().getFullName());
        }
    }
}

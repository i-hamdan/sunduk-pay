package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.exception.*;
import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.PaymentService;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.Validations;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class TransferService implements WalletOperation {

    private final Validations validations;
    private final TransactionRepository transactionRepository;
    private final MainWalletRepository mainWalletRepository;
    private final PaymentService paymentService;
    private final TransactionMapper transactionMapper;
private final KafkaTemplate<String,TransactionEvent> kafkaTemplate;
    public TransferService(Validations validations, TransactionRepository transactionRepository, MainWalletRepository mainWalletRepository, PaymentService paymentService, TransactionMapper transactionMapper, KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.validations = validations;
        this.transactionRepository = transactionRepository;
        this.mainWalletRepository = mainWalletRepository;
        this.paymentService = paymentService;
        this.transactionMapper = transactionMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.TRANSFER_MONEY;
    }

    @Override
    public MainWalletResponse perform(MainWalletRequest mainWalletRequest) {
        try {
            log.info("Performing transfer request for UUID: {}, Request: {}", mainWalletRequest.getUuid(), mainWalletRequest);

            User user = validations.getUserInfo(mainWalletRequest.getUuid());
            log.debug("Fetched user: {}", user);

            MainWallet mainWallet = validations.getMainWalletInfo(user.getUuid());
            log.debug("Fetched main wallet: {}", mainWallet.getMainWalletId());


            WalletWrapper sourceWallet = getWallet(mainWallet, mainWalletRequest.getSourceWalletId());
            Double previousSourceWalletBalance = (sourceWallet == null) ? null : sourceWallet.getBalance();
            log.debug("Source wallet: {}, Previous balance: {}", sourceWallet, previousSourceWalletBalance);

            WalletWrapper targetWallet = getWallet(mainWallet, mainWalletRequest.getTargetWalletId());
            Double previousTargetWalletBalance = (targetWallet == null) ? null : targetWallet.getBalance();
            log.debug("Target wallet: {}, Previous balance: {}", targetWallet, previousTargetWalletBalance);

            boolean sourceExists = (sourceWallet != null);
            boolean targetExists = (targetWallet != null);

            if (sourceExists && targetExists) {
                log.info("Processing internal transfer");
                return handleInternalTransfer(user, mainWallet, mainWalletRequest.getAmount(),
                        sourceWallet, targetWallet, previousSourceWalletBalance, previousTargetWalletBalance);
            } else if (sourceExists && !targetExists || sourceExists && mainWalletRequest.getPaymentMethod() == PaymentMethod.UPI || sourceExists && mainWalletRequest.getPaymentMethod() == PaymentMethod.Bank) {
                log.info("Processing external outgoing transfer");
                return handleExternalOutGoingTransfer(sourceWallet, targetWallet, mainWalletRequest.getAmount(), user);
            } else if (!sourceExists && targetExists || !sourceExists && mainWalletRequest.getPaymentMethod() == PaymentMethod.UPI || !sourceExists && mainWalletRequest.getPaymentMethod() == PaymentMethod.Bank) {
                log.info("Processing external incoming transfer");
                return handleExternalIncomingTransfer(user, mainWalletRequest.getAmount(), targetWallet, sourceWallet);
            } else {
                log.error("Both source and target wallets are invalid for UUID: {}", user.getUuid());
                throw new InvalidPayloadException("both sourceId and targetId is invalid for this user");
            }
        } catch (Exception e) {
            log.error("message : {}",e.getMessage());
            throw e;
        }
    }

    private MainWalletResponse handleExternalIncomingTransfer(User user, Double amount, WalletWrapper targetWallet, WalletWrapper sourceWallet) {
        log.info("Creating checkout session for incoming transfer, Amount: {}", amount);
        return paymentService.createCheckoutSession(user.getUuid(), amount, TransactionType.CREDIT, targetWallet, sourceWallet);
    }

    private MainWalletResponse handleExternalOutGoingTransfer(WalletWrapper sourceSubWallet, WalletWrapper targetWallet, Double amount, User user) {
        log.info("Processing outgoing transfer, Amount: {}", amount);
        validations.validateBalance(sourceSubWallet.getBalance(), amount);
        return paymentService.createCheckoutSession(user.getUuid(), amount, TransactionType.DEBIT, targetWallet, sourceSubWallet);
    }

    public MainWalletResponse handleInternalTransfer(User user, MainWallet mainWallet, Double amount,
                                                     WalletWrapper sourceWallet, WalletWrapper targetWallet,
                                                     Double previousSourceWalletBalance, Double previousTargetWalletBalance) {
        try {
            log.info("Starting internal transfer of amount {} from {} to {}", amount, sourceWallet.getId(), targetWallet.getId());

            List<Transaction> transactions = new ArrayList<>();

            validations.validateBalance(sourceWallet.getBalance(), amount);

            sourceWallet.setBalance(sourceWallet.getBalance() - amount);
            Double newSourceWalletBalance = sourceWallet.getBalance();

            Transaction debitTransaction = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .user(user)
                    .amount(amount)
                    .transactionType(TransactionType.DEBIT)
                    .transactionLevel(TransactionLevel.INTERNAL)
                    .description("Sent to wallet : "+targetWallet.getName())
                    .dateTime(LocalDateTime.now())
                    .mainWallet(mainWallet)
                    .user(mainWallet.getUser())
                    .fromWallet(sourceWallet.getName())
                    .fromWalletId(sourceWallet.getId())
                    .toWallet(targetWallet.getName())
                    .toWalletId(targetWallet.getId()).build();

            transactions.add(debitTransaction);

            targetWallet.setBalance(targetWallet.getBalance() + amount);
            Double newTargetWalletBalance = targetWallet.getBalance();

            Transaction creditTransaction = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .user(user)
                    .amount(amount)
                    .transactionType(TransactionType.CREDIT)
                    .transactionLevel(TransactionLevel.INTERNAL)
                    .description("Received from wallet : "+sourceWallet.getName())
                    .dateTime(LocalDateTime.now())
                    .mainWallet(mainWallet)
                    .user(mainWallet.getUser())
                    .fromWallet(sourceWallet.getName())
                    .fromWalletId(sourceWallet.getId())
                    .toWallet(targetWallet.getName())
                    .toWalletId(targetWallet.getId()).build();
            transactions.add(creditTransaction);

            transactionRepository.saveAll(transactions);
            mainWallet.getTransactionHistory().addAll(transactions);
            mainWalletRepository.save(mainWallet);

            TransactionEvent transactionEvent = transactionMapper.toTransactionEvent(creditTransaction);
          kafkaTemplate.send("transaction-topic",transactionEvent);

            log.info("Internal transfer completed successfully");
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
            log.error("message : {}",e.getMessage());
            throw e;
        }
    }

    private WalletWrapper getWallet(MainWallet mainWallet, String walletId) {

        if (walletId.equals(mainWallet.getMainWalletId())) {
            log.debug("Returning main wallet wrapper for wallet ID {}", walletId);
            return new WalletWrapper(mainWallet);
        }
        log.debug("Requested wallet ID {} does not match MainWallet. Validating sub wallet.", walletId);

        SubWallet subWallet = validations.findSubWalletIfExists(mainWallet, walletId);
        log.debug("Returning sub wallet wrapper for wallet ID {}", walletId);
        if (subWallet != null) {
            log.debug("SubWallet found for wallet ID {}. Returning SubWallet wrapper.", walletId);
            return new WalletWrapper(subWallet);
        } else {
            log.warn("No SubWallet found for wallet ID {} in MainWalletId {}.", walletId, mainWallet.getMainWalletId());
            return null;
        }
    }
}

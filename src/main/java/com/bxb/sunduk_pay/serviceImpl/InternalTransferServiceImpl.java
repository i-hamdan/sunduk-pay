package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.InternalTransferService;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.Validations;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class InternalTransferServiceImpl implements InternalTransferService {
    private final Validations validations;
    private final TransactionRepository transactionRepository;
    private final MainWalletRepository mainWalletRepository;
    private final TransactionMapper transactionMapper;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public InternalTransferServiceImpl(Validations validations, TransactionRepository transactionRepository,
                                       MainWalletRepository mainWalletRepository,
                                       TransactionMapper transactionMapper,
                                       KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.validations = validations;
        this.transactionRepository = transactionRepository;
        this.mainWalletRepository = mainWalletRepository;
        this.transactionMapper = transactionMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public MainWalletResponse doInternalTransfer(User user, MainWallet mainWallet, Double amount,
                                                 WalletWrapper sourceWallet, WalletWrapper targetWallet,
                                                 Double previousSourceWalletBalance, Double previousTargetWalletBalance) {
        try {
            log.info("Starting internal transfer of amount {} from {} to {}", amount, sourceWallet.getId(), targetWallet.getId());

            List<Transaction> transactions = new ArrayList<>();

            log.debug("Validating source wallet balance: currentBalance={}, transferAmount={}",
                    sourceWallet.getBalance(), amount);

            validations.validateBalance(sourceWallet.getBalance(), amount);
            log.info("Balance validation successful");

            log.info("Deducting {} from source wallet {}", amount, sourceWallet.getId());
            sourceWallet.setBalance(sourceWallet.getBalance() - amount);
            Double newSourceWalletBalance = sourceWallet.getBalance();
            log.info("Updated source wallet balance: {}", newSourceWalletBalance);

            String groupId = UUID.randomUUID().toString();
            log.debug("Generated transaction groupId={}", groupId);

            log.info("Creating debit transaction for sourceWallet={}", sourceWallet.getId());

            Transaction debitTransaction = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .groupId(groupId)
                    .user(user)
                    .amount(amount)
                    .transactionType(TransactionType.DEBIT)
                    .transactionLevel(TransactionLevel.INTERNAL)
                    .description("Sent to " + targetWallet.getName())
                    .dateTime(LocalDateTime.now())
                    .mainWallet(mainWallet)
                    .fromWallet(sourceWallet.getName())
                    .fromWalletId(sourceWallet.getId())
                    .toWallet(targetWallet.getName())
                    .toWalletId(targetWallet.getId()).build();
            transactions.add(debitTransaction);
            log.info("Debit transaction created: {}", debitTransaction.getTransactionId());

            log.info("Adding {} to target wallet {}", amount, targetWallet.getId());
            targetWallet.setBalance(targetWallet.getBalance() + amount);
            Double newTargetWalletBalance = targetWallet.getBalance();
            log.info("Updated target wallet balance: {}", newTargetWalletBalance);

            log.info("Creating credit transaction for targetWallet={}", targetWallet.getId());
            Transaction creditTransaction = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .groupId(groupId)
                    .user(user)
                    .amount(amount)
                    .transactionType(TransactionType.CREDIT)
                    .transactionLevel(TransactionLevel.INTERNAL)
                    .description("Received from " + sourceWallet.getName())
                    .dateTime(LocalDateTime.now())
                    .mainWallet(mainWallet)
                    .fromWallet(sourceWallet.getName())
                    .fromWalletId(sourceWallet.getId())
                    .toWallet(targetWallet.getName())
                    .toWalletId(targetWallet.getId()).build();
            transactions.add(creditTransaction);
            log.info("Credit transaction created: {}", creditTransaction.getTransactionId());


            log.debug("Saving transactions into repository, count={}", transactions.size());
            transactionRepository.saveAll(transactions);
            log.info("Transactions saved successfully");

            log.debug("Updating main wallet with new transactions");
            mainWallet.getTransactionHistory().addAll(transactions);
            mainWalletRepository.save(mainWallet);
            log.info("Main wallet updated successfully");

            log.info("Publishing transaction event to Kafka topic 'transaction-topic'");
            TransactionEvent transactionEvent = transactionMapper.toTransactionEvent(creditTransaction);
            kafkaTemplate.send("transaction-topic", transactionEvent);
            log.info("Transaction event published to Kafka successfully");


            log.info("Internal transfer completed successfully for user {}", user.getUuid());
            return MainWalletResponse.builder()
                    .status("SUCCESS")
                    .sourceTransactionId(transactions.get(0).getTransactionId())
                    .targetTransactionId(transactions.get(1).getTransactionId())
                    .transactionGroupId(groupId)
                    .previousSourceWalletBalance(previousSourceWalletBalance)
                    .newSourceWalletBalance(newSourceWalletBalance)
                    .previousTargetWalletBalance(previousTargetWalletBalance)
                    .newTargetWalletBalance(newTargetWalletBalance)
                    .message("Transfer successful")
                    .build();
        } catch (Exception e) {
            log.error("Internal transfer failed for user {}, amount {}, error: {}",
                    user.getUuid(), amount, e.getMessage(), e);
            throw e;
        }

    }
}

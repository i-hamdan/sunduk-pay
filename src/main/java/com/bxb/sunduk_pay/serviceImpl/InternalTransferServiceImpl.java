package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.kafkaEvents.GoalCompletionEvent;
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

/**
 * Service implementation for performing internal transfers between wallets.
 * Handles balance deduction, transaction creation, goal milestone checks, and
 * Kafka event publishing.
 */
@Service
@Log4j2
public class InternalTransferServiceImpl implements InternalTransferService {

    private static final int GOAL_MILESTONE_50 = 50;
    private static final int GOAL_MILESTONE_75 = 75;
    private static final int GOAL_MILESTONE_100 = 100;
    private static final double PERCENT_50 = 0.5;
    private static final double PERCENT_75 = 0.75;

    /** Validation utility for wallet balances */
    private final Validations validations;

    /** Repository for storing transactions */
    private final TransactionRepository transactionRepository;

    /** Repository for storing main wallet updates */
    private final MainWalletRepository mainWalletRepository;

    /** Mapper for transaction to event conversion */
    private final TransactionMapper transactionMapper;

    /** Kafka template for transaction events */
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    /** Kafka template for goal completion events */
    private final KafkaTemplate<String, GoalCompletionEvent> kafkaGoalTemplate;

    /**
     * Constructs the service with required dependencies.
     *
     * @param validations           validation utility
     * @param transactionRepository repository for transactions
     * @param mainWalletRepository  repository for main wallet
     * @param transactionMapper     mapper for transactions
     * @param kafkaTemplate         kafka template for transaction events
     * @param kafkaGoalTemplate     kafka template for goal events
     */
    public InternalTransferServiceImpl(final Validations validations,
                                       final TransactionRepository transactionRepository,
                                       final MainWalletRepository mainWalletRepository,
                                       final TransactionMapper transactionMapper,
                                       final KafkaTemplate<String, TransactionEvent> kafkaTemplate,
                                       final KafkaTemplate<String, GoalCompletionEvent> kafkaGoalTemplate) {
        this.validations = validations;
        this.transactionRepository = transactionRepository;
        this.mainWalletRepository = mainWalletRepository;
        this.transactionMapper = transactionMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaGoalTemplate = kafkaGoalTemplate;
    }

    /**
     * Performs an internal transfer between wallets or sub-wallets for a user.
     *
     * @param user                        the user performing transfer
     * @param mainWallet                  user's main wallet
     * @param amount                      transfer amount
     * @param sourceWallet                source wallet
     * @param targetWallet                target wallet
     * @param previousSourceWalletBalance previous source balance
     * @param previousTargetWalletBalance previous target balance
     * @return MainWalletResponse with transaction details
     */
    @Transactional
    public MainWalletResponse doInternalTransfer(final User user,
                                                 final MainWallet mainWallet,
                                                 final Double amount,
                                                 final WalletWrapper sourceWallet,
                                                 final WalletWrapper targetWallet,
                                                 final Double previousSourceWalletBalance,
                                                 final Double previousTargetWalletBalance) {
        try {
            log.info("Starting internal transfer {} from {} to {}",
                    amount, sourceWallet.getId(), targetWallet.getId());

            final List<Transaction> transactions = new ArrayList<>();
            validations.validateBalance(sourceWallet.getBalance(), amount);

            // Deduct from source
            sourceWallet.setBalance(sourceWallet.getBalance() - amount);
            final double newSourceBalance = sourceWallet.getBalance();

            // Generate transaction group ID
            final String groupId = UUID.randomUUID().toString();

            // Create debit transaction
            final Transaction debitTransaction = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .groupId(groupId)
                    .user(user)
                    .status("SUCCESS")
                    .amount(amount)
                    .transactionType(TransactionType.DEBIT)
                    .transactionLevel(TransactionLevel.INTERNAL)
                    .description("Sent to " + targetWallet.getName())
                    .dateTime(LocalDateTime.now())
                    .mainWallet(mainWallet)
                    .fromWallet(sourceWallet.getName())
                    .fromWalletId(sourceWallet.getId())
                    .toWallet(targetWallet.getName())
                    .toWalletId(targetWallet.getId())
                    .build();
            transactions.add(debitTransaction);

            // Add to target
            targetWallet.setBalance(targetWallet.getBalance() + amount);
            final double newTargetBalance = targetWallet.getBalance();

            // Check goal milestones
            checkGoalMilestones(user, targetWallet, previousTargetWalletBalance);

            // Create credit transaction
            final Transaction creditTransaction = Transaction.builder()
                    .transactionId(UUID.randomUUID().toString())
                    .groupId(groupId)
                    .user(user)
                    .amount(amount)
                    .status("SUCCESS")
                    .transactionType(TransactionType.CREDIT)
                    .transactionLevel(TransactionLevel.INTERNAL)
                    .description("Received from " + sourceWallet.getName())
                    .dateTime(LocalDateTime.now())
                    .mainWallet(mainWallet)
                    .fromWallet(sourceWallet.getName())
                    .fromWalletId(sourceWallet.getId())
                    .toWallet(targetWallet.getName())
                    .toWalletId(targetWallet.getId())
                    .build();
            transactions.add(0, creditTransaction);

            // Save transactions
            transactionRepository.saveAll(transactions);

            // Update main wallet
            mainWallet.getTransactionHistory().addAll(transactions);
            mainWalletRepository.save(mainWallet);

            // Publish transaction event
            final TransactionEvent transactionEvent = transactionMapper.toTransactionEvent(creditTransaction);
            kafkaTemplate.send("transaction-topic", transactionEvent);

            return MainWalletResponse.builder()
                    .status("SUCCESS")
                    .sourceTransactionId(transactions.get(0).getTransactionId())
                    .targetTransactionId(transactions.get(1).getTransactionId())
                    .transactionGroupId(groupId)
                    .previousSourceWalletBalance(previousSourceWalletBalance)
                    .newSourceWalletBalance(newSourceBalance)
                    .previousTargetWalletBalance(previousTargetWalletBalance)
                    .newTargetWalletBalance(newTargetBalance)
                    .message("Transfer successful")
                    .build();
        } catch (final Exception e) {
            log.error("Internal transfer failed for user {}, amount {}, error: {}",
                    user.getUuid(), amount, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Checks and sends goal milestone events for a wallet.
     *
     * @param user                     the user
     * @param wallet                   the wallet
     * @param previousTargetWalletBalance previous balance
     */
    private void checkGoalMilestones(final User user,
                                     final WalletWrapper wallet,
                                     final Double previousTargetWalletBalance) {
        final Double goalAmount = wallet.getGoalAmount();
        if (goalAmount == null || goalAmount <= 0) return;

        final double completionPercent = (wallet.getBalance() / goalAmount) * 100;

        if (completionPercent >= GOAL_MILESTONE_50
                && previousTargetWalletBalance < goalAmount * PERCENT_50) {
            sendGoalCompletionEvent(user, wallet, GOAL_MILESTONE_50);
        }
        if (completionPercent >= GOAL_MILESTONE_75
                && previousTargetWalletBalance < goalAmount * PERCENT_75) {
            sendGoalCompletionEvent(user, wallet, GOAL_MILESTONE_75);
        }
        if (completionPercent >= GOAL_MILESTONE_100
                && previousTargetWalletBalance < goalAmount) {
            sendGoalCompletionEvent(user, wallet, GOAL_MILESTONE_100);
        }
    }

    /**
     * Sends a goal completion event to Kafka for a milestone.
     *
     * @param user      the user
     * @param wallet    the wallet
     * @param milestone milestone percentage
     */
    private void sendGoalCompletionEvent(final User user,
                                         final WalletWrapper wallet,
                                         final int milestone) {
        final GoalCompletionEvent event = GoalCompletionEvent.builder()
                .userId(user.getUuid())
                .email(user.getEmail())
                .walletId(wallet.getId())
                .walletName(wallet.getName())
                .milestone(milestone)
                .currentBalance(wallet.getBalance())
                .goalAmount(wallet.getGoalAmount())
                .timestamp(LocalDateTime.now())
                .build();
        kafkaGoalTemplate.send("goal-completion-topic", event);
    }
}

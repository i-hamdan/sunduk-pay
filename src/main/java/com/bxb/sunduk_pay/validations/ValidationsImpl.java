package com.bxb.sunduk_pay.validations;


import com.bxb.sunduk_pay.exception.*;
import com.bxb.sunduk_pay.repository.*;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.MasterWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.User;

import java.util.Optional;

/**
 * Implements validation logic for users, wallets, and transactions.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class ValidationsImpl implements Validations {
    /** Repository for accessing user data. */
    private final UserRepository userRepository;
    /** Repository for accessing main wallet data. */
    private final MainWalletRepository mainWalletRepository;
    /** Repository for accessing transaction data. */
    private final TransactionRepository transactionRepository;
    /** Repository for accessing master wallet data. */
    private final MasterWalletRepository masterWalletRepository;
    /** Repository for accessing sub-wallet data. */
    private final SubWalletRepository subWalletRepository;
    /** Maximum allowed number of sub-wallets. */
private static final int WALLET_SIZE = 19;


    /** {@inheritDoc} */
    public User getUserInfo(
            final String uuid) {
        log.info("Fetching user with UUID: {}", uuid);
        return userRepository.findById(uuid)
                .orElseThrow(() -> {
                    log.error("User not found with UUID: {}", uuid);
                    return new UserNotFoundException(
                            "User not found with UUID: " + uuid);
                });
    }


    /** {@inheritDoc} */

    @Override
    public MainWallet getMainWalletByWalletId(
            final String walletId) {
      return mainWalletRepository.
              findById(walletId)
              .orElseThrow(() -> new ResourceNotFoundException(
                      "Cannot find mainWallet By Id : " + walletId));
    }


    /** {@inheritDoc} */

    public MainWallet getMainWalletInfo(
            final String uuid) {
        log.info("Fetching mainWallet with UUID : {}",
                uuid);
        return mainWalletRepository.findByUserUuid(uuid)
                .orElseThrow(() -> {
                    log.error("User not found with UUID: {}", uuid);
                    return new WalletNotFoundException(
                            "Wallet not found for user: " + uuid);
                });
    }

    /** {@inheritDoc} */

    @Override
    public void validateNumberOfSubWallets(final int size) {
        log.info("Validating number of SubWallets: {}", size);
        if (size <= WALLET_SIZE) {
            log.info(
                    "Validation passed. Current subwallet count: {}",
                    size);
        } else {
            log.error(
"Validation failed. Maximum allowed subwallets: 20, provided: {}",
                    size);
            throw new MaxSubWalletsExceededException(
                    "Maximum 20 sub wallets are allowed.");
        }
    }


    /**
     * Retrieves a paginated list of transactions for a specific user.
     * <p>
     * This method supports multiple optional filters:
     * <ul>
     *   <li><b>walletId</b> – filters transactions for that specific wallet.</li>
     *   <li><b>transactionType</b> – filters by CREDIT or DEBIT transactions.</li>
     *   <li><b>paymentMethod</b> – filters by a specific payment method
     *       (e.g., UPI, CARD, etc.).</li>
     * </ul>
     * <p>
     * The logic prioritizes the most specific filters first
     * (wallet + type + method) and falls back to broader searches
     * when some filters are missing.
     * <p>
     * Throws {@link TransactionNotFoundException} if no transactions are found.
     *
     * @param uuid            unique user identifier
     * @param walletId        optional wallet identifier
     * @param method          optional payment method
     * @param transactionType optional transaction type (CREDIT/DEBIT)
     * @param pageable        pagination information
     * @return a paginated list of {@link Transaction} objects
     * @throws TransactionNotFoundException if no matching transactions are found
     */
    @Override
    public Page<Transaction> getTransactions(
            final String uuid,
            final String walletId,
            final PaymentMethod method,
            final TransactionType transactionType,
            final Pageable pageable) {

        if (uuid == null) {
            throw new InvalidPayloadException("User UUID cannot be null");
        }

        Page<Transaction> transactions;

        boolean hasWallet = walletId != null;
        boolean hasType = transactionType != null;
        boolean hasMethod = method != null;

        // Case 1: Wallet is provided
        if (hasWallet) {
            // Case 1a: Debit transactions
            if (hasType && transactionType == TransactionType.DEBIT) {
                if (hasMethod) {
                    transactions = transactionRepository
                            .findByUserUuidAndFromWalletIdAndTransactionTypeAndPaymentMethod(
                                    uuid, walletId, TransactionType.DEBIT, method, pageable);
                } else {
                    transactions = transactionRepository
                            .findByUserUuidAndFromWalletIdAndTransactionType(
                                    uuid, walletId, TransactionType.DEBIT, pageable);
                }
            }

            // Case 1b: Credit transactions
            else if (hasType && transactionType == TransactionType.CREDIT) {
                if (hasMethod) {
                    transactions = transactionRepository
                            .findByUserUuidAndToWalletIdAndTransactionTypeAndPaymentMethod(
                                    uuid, walletId, TransactionType.CREDIT, method, pageable);
                } else {
                    transactions = transactionRepository
                            .findByUserUuidAndToWalletIdAndTransactionType(
                                    uuid, walletId, TransactionType.CREDIT, pageable);
                }
            }

            // Case 1c: No transaction type filter
            else {
                if (hasMethod) {
                    transactions = transactionRepository
                            .findByUuidAndWalletIdAndPaymentMethod(
                                    uuid, walletId, method, pageable);
                } else {
                    transactions = transactionRepository
                            .findAllByUserUuidAndWalletId(
                                    uuid, walletId, pageable);
                }
            }
        }

        // Case 2: No wallet filter
        else {
            if (hasType && hasMethod) {
                transactions = transactionRepository
                        .findByUserUuidAndTransactionTypeAndPaymentMethodAndIsMasterFalse(
                                uuid, transactionType, method, pageable);
            } else if (hasType) {
                transactions = transactionRepository
                        .findByUserUuidAndTransactionTypeAndIsMasterFalse(
                                uuid, transactionType, pageable);
            } else if (hasMethod) {
                transactions = transactionRepository
                        .findByUserUuidAndPaymentMethodAndIsMasterFalse(
                                uuid, method, pageable);
            } else {
                transactions = transactionRepository
                        .findByUserUuidAndIsMasterFalse(uuid, pageable);
            }
        }

        // Throw exception if no records found
        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException(
                    "No transactions found for User UUID: " + uuid);
        }

        return transactions;
    }


    /** {@inheritDoc} */
    @Override
    public void validateBalance(
          final Double balance,
          final Double amount) {
        log.info(
      "Validating transaction with balance: {} and amount: {}",
      balance, amount);
        if (balance == null || amount == null) {
            log.error(
  "Validation failed: Balance or amount is null. Balance={},Amount={}",
  balance, amount);
            throw new NullAmountException(
                    "Balance and amount must not be null. Provided balance="
                            + balance + ", amount=" + amount);
        }
        if (balance < amount) {
            log.error(
                    "Validation failed: Insufficient balance."
                            + " Available={}, Required={}",
                    balance, amount);
            throw new InsufficientBalanceException(
                    "Insufficient funds: required="
                            + amount + ", available="
                            + balance);
        }
        log.debug(
 "Validation successful:  Balance={}, Amount={}",
 balance, amount);
    }

    /** {@inheritDoc} */
    @Override
    public SubWallet findSubWalletIfExists(
            final String mainWalletId,
            final String subWalletId) {
        if (subWalletId == null) {
            log.error(
 "Validation failed:subWalletId is null."
            );
            throw new NullValueException(
                    "subWalletId must not be null.");
        }
        log.info(
  "Searching for SubWallet with ID: {} ", subWalletId);
        return subWalletRepository
        .findBySubWalletIdAndMainWallet_MainWalletIdAndIsDeletedFalse(
               subWalletId, mainWalletId)
                .orElseGet(() -> {
                    log.warn(
  "SubWallet not found. SubWalletId={} ",
  subWalletId);
                    return null;
                });
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable("fromIcons")
    public String getFromIconOfTxn(
            final String mainWalletId, final String fromWalletId) {
        MainWallet mainWallet = getMainWalletByWalletId(mainWalletId);
        SubWallet subWallet = mainWallet.getSubWallets()
                .stream().filter(pot -> pot.getSubWalletId()
                        .equals(fromWalletId)).findFirst().orElse(null);
        if (subWallet != null) {
            return subWallet.getIcon();
        } else if (mainWallet.getMainWalletId().equals(fromWalletId)) {
            return "mainWallet";
        } else {
            return "external";
        }
    }

    /** {@inheritDoc} */
    @Override
    @Cacheable("ToIcons")
    public String getToIconOfTxn(
            final String mainWalletId, final String toWalletId) {
        MainWallet mainWallet = getMainWalletByWalletId(mainWalletId);
        SubWallet subWallet = mainWallet
                .getSubWallets().stream()
                .filter(pot -> pot.getSubWalletId()
                        .equals(toWalletId)).findFirst()
                .orElse(null);
        if (subWallet != null) {
            return subWallet.getIcon();
        } else if (mainWallet.getMainWalletId().equals(toWalletId)) {
            return "mainWallet";
        } else {
            return "external";
        }
    }

    /** {@inheritDoc} */
    @Override
    public Boolean removeSubwallet(
            final MainWallet wallet, final String subWalletId) {
        return wallet.getSubWallets().removeIf(subwallet ->
                subwallet.getSubWalletId().equals(subWalletId));
    }

    /** {@inheritDoc} */
    @Override
    public MasterWallet getMasterWalletInfo(final String uuid) {
        log.info("Fetching MasterWallet for User UUID: {}", uuid);
        return masterWalletRepository.findByUserUuid(uuid).orElseThrow(() -> {
            log.error("User not found with ID: {}", uuid);
            return new UserNotFoundException("User not found with ID: " + uuid);
        });
    }

    /**method to find subwallet by name.
     * {@inheritDoc} */
    @Override
    public void findSubWalletByName(
            final String subWalletName, String mainWalletId) {
        Optional<SubWallet> subWallet = subWalletRepository
       .findActiveByNameAndMainWallet(subWalletName,mainWalletId);
        if (subWallet.isPresent()) {
            throw new SubWalletAlreadyExistsException(
                    "SubWallet with name "
                            + subWalletName
                            + " already exists.");
        }
    }

    /**This method validates the target balance.
     * {@inheritDoc} */
    @Override
    public void validateTargetBalance(
            final Double balance,
            final Double amount) {
        log.info(
                "Validating targetBalance of subWallet"
                        + " targetBalance=" + balance
                        + " and amount=" + amount);
        if (balance == null || amount == null) {
            log.error(
"Validation failed: Balance or amount is null. Balance={},Amount={}",
                    balance, amount);
            throw new NullAmountException(
                    "Balance and amount must not be null. Provided balance="
                            + balance + ", amount=" + amount);
        }
        if (balance < amount) {
            log.error(
                    "Validation failed: Amount is greater than "
                            + "targetBalance. targetBalance="
                            + balance + " amount=" + amount);

            throw new InsufficientBalanceException(
                    "Amount is greater than "
                    + "targetBalance. targetBalance="
                    + balance + " amount=" + amount);
        }
        log.debug(
"Validation successful:  targetBalance={}, Amount={}",
                balance, amount);

    }
}

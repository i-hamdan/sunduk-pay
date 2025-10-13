package com.bxb.sunduk_pay.validations;


import com.bxb.sunduk_pay.exception.MaxSubWalletsExceededException;
import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.exception.WalletNotFoundException;
import com.bxb.sunduk_pay.exception.SubWalletAlreadyExistsException;
import com.bxb.sunduk_pay.repository.MasterWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import com.bxb.sunduk_pay.exception.InsufficientBalanceException;
import com.bxb.sunduk_pay.exception.NullAmountException;
import com.bxb.sunduk_pay.exception.NullValueException;
import com.bxb.sunduk_pay.exception.TransactionNotFoundException;
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

    /** {@inheritDoc} */

    @Override
    public Page<Transaction> validateTransactionsByUuidAndSubWalletId(
       final String uuid,
       final String walletId,
       final String transactionGroupId,
       final PaymentMethod method,
       final TransactionType transactionType,
       final  Pageable pageable) {

        if (transactionGroupId != null) {
            return transactionRepository
                    .findByUuidAndGroupId(
                            uuid, transactionGroupId, pageable);
        }

        Page<Transaction> transactions;
        if (walletId != null) {
            // Subwallet is specified
            if (transactionType == TransactionType.DEBIT) {
                // Only those where subWalletId is the FROM wallet
                if (method != null) {
                    transactions = transactionRepository
.findByUuidAndFromWalletIdAndTransactionTypeAndPaymentMethod(
uuid, walletId, TransactionType.DEBIT, method, pageable);
                } else {
                    transactions = transactionRepository
                            .findByUuidAndFromWalletIdAndTransactionType(
                            uuid, walletId, TransactionType.DEBIT, pageable);
                }
            } else if (transactionType == TransactionType.CREDIT) {
                // Only those where subWalletId is the TO wallet
                if (method != null) {
                    transactions = transactionRepository
.findByUuidAndToWalletIdAndTransactionTypeAndPaymentMethod(
uuid, walletId, TransactionType.CREDIT, method, pageable);
                } else {
                    transactions = transactionRepository
                            .findByUuidAndToWalletIdAndTransactionType(
                            uuid, walletId, TransactionType.CREDIT, pageable);
                }
            } else {
                if (method != null) {
                    transactions = transactionRepository.
                            findByUuidAndWalletIdAndPaymentMethod(
                            uuid, walletId, method, pageable);
                } else {
                    transactions = transactionRepository
                            .findAllByUuidAndWallet(
                                    uuid, walletId, pageable);
                }
            }
        } else {
            // No subwallet filter
            if (transactionType != null) {
                if (method != null) {
                    transactions = transactionRepository
   .findByUuidAndTransactionTypeAndPaymentMethodAndIsMasterFalse(
                            uuid, transactionType, method, pageable);
                } else {
                    transactions = transactionRepository
                            .findByUuidAndTransactionTypeAndIsMasterFalse(
                            uuid, transactionType, pageable);
                }
            } else {
                if (method != null) {
                    transactions = transactionRepository
                            .findByUuidAndPaymentMethodAndIsMasterFalse(
                            uuid, method, pageable);
                } else {
                    transactions = transactionRepository
                            .findByUuidAndIsMasterFalse(
                                    uuid, pageable);
                }
            }
        }

        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException(
                    "No transactions found for User UUID: "
                            + uuid);
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
            final MainWallet wallet,
           final String subWalletId) {
        if (wallet == null || subWalletId == null) {
            log.error(
 "Validation failed: wallet or  subWalletId is null."
         + " Wallet = "
         + wallet.getMainWalletId()
         + " SubWalletId = "
         + subWalletId);
            throw new NullValueException(
                    "wallet or subWalletId must not be null.");
        }
        log.info(
  "Searching for SubWallet with ID: {} in MainWallet: {}",
  subWalletId, wallet.getMainWalletId());
        return wallet.getSubWallets().stream()
                .filter(sw -> sw.getSubWalletId().equals(subWalletId))
                .findFirst()
                .orElseGet(() -> {
                    log.warn(
  "SubWallet not found. SubWalletId={} under MainWallet={}",
  subWalletId, wallet.getMainWalletId());
                    return null;
                });
    }

    /** {@inheritDoc} */
    @Override
    public SubWallet getSubWalletIfExists(final MainWallet wallet,
                                          final String subWalletId) {
        log.info(
                "Searching for SubWallet with ID: {} in MainWallet: {}",
                subWalletId, wallet
                        .getMainWalletId());
        return wallet.getSubWallets().stream()
                .filter(sw -> sw.getSubWalletId().equals(subWalletId))
                .findFirst()
                .orElseGet(() -> {
                    log.warn("SubWallet not found."
                       + " SubWalletId={} under MainWallet={}",
                            subWalletId, wallet.getMainWalletId());
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
            final MainWallet mainWallet,
            final String subWalletName) {
        Optional<SubWallet> subWallet = mainWallet
                .getSubWallets().stream()
                .filter(sw -> sw.getSubWalletName()
                        .equalsIgnoreCase(subWalletName))
                .findFirst();
        if (subWallet.isPresent()&&!subWallet
                .get().getIsDeleted()) {
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

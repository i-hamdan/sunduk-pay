package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.exceptions.InsufficientBalanceException;
import com.bxb.sunduk_pay.exceptions.MaxSubWalletsExceededException;
import com.bxb.sunduk_pay.exceptions.NullAmountException;
import com.bxb.sunduk_pay.exceptions.NullValueException;
import com.bxb.sunduk_pay.exceptions.ResourceNotFoundException;
import com.bxb.sunduk_pay.exceptions.TransactionNotFoundException;
import com.bxb.sunduk_pay.exceptions.UserNotFoundException;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.MasterWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.repository.MasterWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.TransactionType;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * Implements validation logic for users, wallets, and transactions.
 */
@Log4j2
@Component
public class ValidationsImpl implements Validations {

    /** Maximum number of sub-wallets allowed per main wallet. */
    private static final int MAX_SUB_WALLETS = 19;

    /** Repository for User entities. */
    private final UserRepository userRepository;

    /** Repository for MainWallet entities. */
    private final MainWalletRepository mainWalletRepository;

    /** Repository for Transaction entities. */
    private final TransactionRepository transactionRepository;

    /** Repository for MasterWallet entities. */
    private final MasterWalletRepository masterWalletRepository;

    /**
     * Constructor with required repositories.
     */
    public ValidationsImpl(final UserRepository userRepository,
                           final MainWalletRepository mainWalletRepository,
                           final TransactionRepository transactionRepository,
                           final MasterWalletRepository masterWalletRepository) {
        this.userRepository = userRepository;
        this.mainWalletRepository = mainWalletRepository;
        this.transactionRepository = transactionRepository;
        this.masterWalletRepository = masterWalletRepository;
    }

    /** {@inheritDoc} */
    @Override
    public User getUserInfo(final String uuid) {
        log.info("Fetching user with UUID: {}", uuid);
        return userRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with UUID: " + uuid
                ));
    }

    /** {@inheritDoc} */
    @Override
    public MainWallet getMainWalletByWalletId(final String walletId) {
        log.info("Fetching main wallet with ID: {}", walletId);
        return mainWalletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cannot find mainWallet By Id: " + walletId
                ));
    }

    /** {@inheritDoc} */
    @Override
    public MainWallet getMainWalletInfo(final String uuid) {
        log.info("Fetching main wallet for User UUID: {}", uuid);
        return mainWalletRepository.findByUser_Uuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Wallet not found for user: " + uuid
                ));
    }

    /** {@inheritDoc} */
    @Override
    public void validateNumberOfSubWallets(final int size) {
        log.info("Validating number of sub-wallets: {}", size);
        if (size > MAX_SUB_WALLETS) {
            throw new MaxSubWalletsExceededException(
                    "Maximum " + MAX_SUB_WALLETS + " sub wallets are allowed."
            );
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
            final Pageable pageable
    ) {
        Page<Transaction> transactions;
        if (transactionGroupId != null) {
            transactions = transactionRepository.findByUser_UuidAndGroupId(
                    uuid, transactionGroupId, pageable
            );
        } else if (walletId != null) {
            transactions = fetchTransactionsByWallet(
                    uuid, walletId, transactionType, method, pageable
            );
        } else {
            transactions = fetchTransactionsWithoutWallet(
                    uuid, transactionType, method, pageable
            );
        }

        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException(
                    "No transactions found for User UUID: " + uuid
            );
        }
        return transactions;
    }

    /** {@inheritDoc} */
    @Override
    public void validateBalance(final Double balance, final Double amount) {
        if (balance == null || amount == null) {
            throw new NullAmountException(
                    "Balance and amount must not be null. Provided balance=" + balance + ", amount=" + amount
            );
        }
        if (balance < amount) {
            throw new InsufficientBalanceException(
                    "Insufficient funds: required=" + amount + ", available=" + balance
            );
        }
    }

    /** {@inheritDoc} */
    @Override
    public SubWallet findSubWalletIfExists(final MainWallet wallet, final String subWalletId) {
        ensureWalletAndSubWalletId(wallet, subWalletId);
        return wallet.getSubWallets().stream()
                .filter(sw -> sw.getSubWalletId().equals(subWalletId))
                .findFirst()
                .orElse(null);
    }

    /** {@inheritDoc} */
    @Override
    public SubWallet getSubWalletIfExists(final MainWallet wallet, final String subWalletId) {
        ensureWalletAndSubWalletId(wallet, subWalletId);
        return wallet.getSubWallets().stream()
                .filter(sw -> sw.getSubWalletId().equals(subWalletId))
                .findFirst()
                .orElse(null);
    }

    /** {@inheritDoc} */
    @Override
    public String getFromIconOfTxn(final String mainWalletId, final String fromWalletId) {
        final MainWallet mainWallet = getMainWalletByWalletId(mainWalletId);
        return getIconForWallet(mainWallet, fromWalletId);
    }

    /** {@inheritDoc} */
    @Override
    public String getToIconOfTxn(final String mainWalletId, final String toWalletId) {
        final MainWallet mainWallet = getMainWalletByWalletId(mainWalletId);
        return getIconForWallet(mainWallet, toWalletId);
    }

    /** {@inheritDoc} */
    @Override
    public Boolean removeSubwallet(final MainWallet wallet, final String subWalletId) {
        ensureWalletAndSubWalletId(wallet, subWalletId);
        return wallet.getSubWallets().removeIf(sw -> sw.getSubWalletId().equals(subWalletId));
    }

    /** {@inheritDoc} */
    @Override
    public MasterWallet getMasterWalletInfo(final String uuid) {
        log.info("Fetching MasterWallet for User UUID: {}", uuid);
        return masterWalletRepository.findByUser_Uuid(uuid)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with ID: " + uuid
                ));
    }

    /* ----------- private helpers ----------- */

    private Page<Transaction> fetchTransactionsByWallet(final String uuid,
                                                        final String walletId,
                                                        final TransactionType transactionType,
                                                        final PaymentMethod method,
                                                        final Pageable pageable) {
        if (transactionType == TransactionType.DEBIT) {
            return (method != null)
                    ? transactionRepository.findByUser_UuidAndFromWalletIdAndTransactionTypeAndPaymentMethod(
                    uuid, walletId, TransactionType.DEBIT, method, pageable
            )
                    : transactionRepository.findByUser_UuidAndFromWalletIdAndTransactionType(
                    uuid, walletId, TransactionType.DEBIT, pageable
            );
        } else if (transactionType == TransactionType.CREDIT) {
            return (method != null)
                    ? transactionRepository.findByUser_UuidAndToWalletIdAndTransactionTypeAndPaymentMethod(
                    uuid, walletId, TransactionType.CREDIT, method, pageable
            )
                    : transactionRepository.findByUser_UuidAndToWalletIdAndTransactionType(
                    uuid, walletId, TransactionType.CREDIT, pageable
            );
        }
        return (method != null)
                ? transactionRepository.findByUser_UuidAndWalletIdAndPaymentMethod(
                uuid, walletId, method, pageable
        )
                : transactionRepository.findAllByUserAndWallet(uuid, walletId, pageable);
    }

    private Page<Transaction> fetchTransactionsWithoutWallet(final String uuid,
                                                             final TransactionType transactionType,
                                                             final PaymentMethod method,
                                                             final Pageable pageable) {
        if (transactionType != null) {
            return (method != null)
                    ? transactionRepository.findByUser_UuidAndTransactionTypeAndPaymentMethodAndIsMasterFalse(
                    uuid, transactionType, method, pageable
            )
                    : transactionRepository.findByUser_UuidAndTransactionTypeAndIsMasterFalse(
                    uuid, transactionType, pageable
            );
        }
        return (method != null)
                ? transactionRepository.findByUser_UuidAndPaymentMethodAndIsMasterFalse(
                uuid, method, pageable
        )
                : transactionRepository.findByUser_UuidAndIsMasterFalse(uuid, pageable);
    }

    private void ensureWalletAndSubWalletId(final MainWallet wallet, final String subWalletId) {
        if (wallet == null || subWalletId == null) {
            throw new NullValueException("wallet or subWalletId must not be null.");
        }
    }

    private String getIconForWallet(final MainWallet mainWallet, final String walletId) {
        final SubWallet subWallet = mainWallet.getSubWallets().stream()
                .filter(sw -> sw.getSubWalletId().equals(walletId))
                .findFirst()
                .orElse(null);
        if (subWallet != null) {
            return subWallet.getIcon();
        }
        if (mainWallet.getMainWalletId().equals(walletId)) {
            return "mainWallet";
        }
        return "external";
    }
}

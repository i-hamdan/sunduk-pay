package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.exception.*;
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

@Log4j2
@Component
public class ValidationsImpl implements Validations {

    private static final int MAX_SUB_WALLETS = 19;

    private final UserRepository userRepository;
    private final MainWalletRepository mainWalletRepository;
    private final TransactionRepository transactionRepository;
    private final MasterWalletRepository masterWalletRepository;

    public ValidationsImpl(UserRepository userRepository,
                           MainWalletRepository mainWalletRepository,
                           TransactionRepository transactionRepository,
                           MasterWalletRepository masterWalletRepository) {
        this.userRepository = userRepository;
        this.mainWalletRepository = mainWalletRepository;
        this.transactionRepository = transactionRepository;
        this.masterWalletRepository = masterWalletRepository;
    }

    @Override
    public User getUserInfo(String uuid) {
        log.info("Fetching user with UUID: {}", uuid);
        return userRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException("User not found with UUID: " + uuid));
    }

    @Override
    public MainWallet getMainWalletByWalletId(String walletId) {
        log.info("Fetching main wallet with ID: {}", walletId);
        return mainWalletRepository.findById(walletId)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot find mainWallet By Id: " + walletId));
    }

    @Override
    public MainWallet getMainWalletInfo(String uuid) {
        log.info("Fetching main wallet for User UUID: {}", uuid);
        return mainWalletRepository.findByUser_Uuid(uuid)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + uuid));
    }

    @Override
    public void validateNumberOfSubWallets(int size) {
        log.info("Validating number of sub-wallets: {}", size);
        if (size > MAX_SUB_WALLETS) {
            throw new MaxSubWalletsExceededException("Maximum " + MAX_SUB_WALLETS + " sub wallets are allowed.");
        }
    }

    @Override
    public Page<Transaction> validateTransactionsByUuidAndSubWalletId(
            String uuid,
            String walletId,
            String transactionGroupId,
            PaymentMethod method,
            TransactionType transactionType,
            Pageable pageable) {

        Page<Transaction> transactions;
        if (transactionGroupId != null) {
            transactions = transactionRepository.findByUser_UuidAndGroupId(uuid, transactionGroupId, pageable);
        } else if (walletId != null) {
            transactions = fetchTransactionsByWallet(uuid, walletId, transactionType, method, pageable);
        } else {
            transactions = fetchTransactionsWithoutWallet(uuid, transactionType, method, pageable);
        }

        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException("No transactions found for User UUID: " + uuid);
        }
        return transactions;
    }

    @Override
    public void validateBalance(Double balance, Double amount) {
        if (balance == null || amount == null) {
            throw new NullAmountException("Balance and amount must not be null. Provided balance=" + balance + ", amount=" + amount);
        }
        if (balance < amount) {
            throw new InsufficientBalanceException("Insufficient funds: required=" + amount + ", available=" + balance);
        }
    }

    @Override
    public SubWallet findSubWalletIfExists(MainWallet wallet, String subWalletId) {
        ensureWalletAndSubWalletId(wallet, subWalletId);
        return wallet.getSubWallets().stream()
                .filter(sw -> sw.getSubWalletId().equals(subWalletId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public SubWallet getSubWalletIfExists(MainWallet wallet, String subWalletId) {
        ensureWalletAndSubWalletId(wallet, subWalletId);
        return wallet.getSubWallets().stream()
                .filter(sw -> sw.getSubWalletId().equals(subWalletId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getFromIconOfTxn(String mainWalletId, String fromWalletId) {
        MainWallet mainWallet = getMainWalletByWalletId(mainWalletId);
        return getIconForWallet(mainWallet, fromWalletId);
    }

    @Override
    public String getToIconOfTxn(String mainWalletId, String toWalletId) {
        MainWallet mainWallet = getMainWalletByWalletId(mainWalletId);
        return getIconForWallet(mainWallet, toWalletId);
    }

    @Override
    public Boolean removeSubwallet(MainWallet wallet, String subWalletId) {
        ensureWalletAndSubWalletId(wallet, subWalletId);
        return wallet.getSubWallets().removeIf(sw -> sw.getSubWalletId().equals(subWalletId));
    }

    @Override
    public MasterWallet getMasterWalletInfo(String uuid) {
        log.info("Fetching MasterWallet for User UUID: {}", uuid);
        return masterWalletRepository.findByUser_Uuid(uuid)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + uuid));
    }

    /* ----------- private helpers ----------- */

    private Page<Transaction> fetchTransactionsByWallet(String uuid,
                                                        String walletId,
                                                        TransactionType transactionType,
                                                        PaymentMethod method,
                                                        Pageable pageable) {
        if (transactionType == TransactionType.DEBIT) {
            return (method != null)
                    ? transactionRepository.findByUser_UuidAndFromWalletIdAndTransactionTypeAndPaymentMethod(uuid, walletId, TransactionType.DEBIT, method, pageable)
                    : transactionRepository.findByUser_UuidAndFromWalletIdAndTransactionType(uuid, walletId, TransactionType.DEBIT, pageable);
        } else if (transactionType == TransactionType.CREDIT) {
            return (method != null)
                    ? transactionRepository.findByUser_UuidAndToWalletIdAndTransactionTypeAndPaymentMethod(uuid, walletId, TransactionType.CREDIT, method, pageable)
                    : transactionRepository.findByUser_UuidAndToWalletIdAndTransactionType(uuid, walletId, TransactionType.CREDIT, pageable);
        }
        return (method != null)
                ? transactionRepository.findByUser_UuidAndWalletIdAndPaymentMethod(uuid, walletId, method, pageable)
                : transactionRepository.findAllByUserAndWallet(uuid, walletId, pageable);
    }

    private Page<Transaction> fetchTransactionsWithoutWallet(String uuid,
                                                             TransactionType transactionType,
                                                             PaymentMethod method,
                                                             Pageable pageable) {
        if (transactionType != null) {
            return (method != null)
                    ? transactionRepository.findByUser_UuidAndTransactionTypeAndPaymentMethodAndIsMasterFalse(uuid, transactionType, method, pageable)
                    : transactionRepository.findByUser_UuidAndTransactionTypeAndIsMasterFalse(uuid, transactionType, pageable);
        }
        return (method != null)
                ? transactionRepository.findByUser_UuidAndPaymentMethodAndIsMasterFalse(uuid, method, pageable)
                : transactionRepository.findByUser_UuidAndIsMasterFalse(uuid, pageable);
    }

    private void ensureWalletAndSubWalletId(MainWallet wallet, String subWalletId) {
        if (wallet == null || subWalletId == null) {
            throw new NullValueException("wallet or subWalletId must not be null.");
        }
    }

    private String getIconForWallet(MainWallet mainWallet, String walletId) {
        SubWallet subWallet = mainWallet.getSubWallets().stream()
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

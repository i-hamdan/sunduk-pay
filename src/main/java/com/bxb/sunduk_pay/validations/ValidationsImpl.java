package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.exception.*;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.MasterWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.util.TransactionType;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class ValidationsImpl implements Validations {
    private final UserRepository userRepository;
    private final MainWalletRepository mainWalletRepository;
    private final TransactionRepository transactionRepository;
    private final MasterWalletRepository masterWalletRepository;


    public ValidationsImpl(UserRepository userRepository, MainWalletRepository mainWalletRepository, TransactionRepository transactionRepository, MasterWalletRepository masterWalletRepository) {
        this.userRepository = userRepository;
        this.mainWalletRepository = mainWalletRepository;
        this.transactionRepository = transactionRepository;
        this.masterWalletRepository = masterWalletRepository;
    }


    public User getUserInfo(String uuid) {
        log.info("Fetching user with UUID: {}", uuid);
        return userRepository.findById(uuid)
                .orElseThrow(() -> {
                    log.error("User not found with UUID: {}", uuid);
                    return new UserNotFoundException("User not found with UUID: " + uuid);
                });
    }

    @Override
    public MainWallet getMainWalletByWalletId(String walletId) {
      return mainWalletRepository.findById(walletId).orElseThrow(()->new ResourceNotFoundException("Cannot find mainWallet By Id : "+walletId));
    }

    public MainWallet getMainWalletInfo(String uuid) {
        log.info("Fetching mainWallet with UUID : {}", uuid);
        return mainWalletRepository.findByUser_Uuid(uuid)
                .orElseThrow(() -> {
                    log.error("User not found with UUID: {}", uuid);
                    return new WalletNotFoundException("Wallet not found for user: " + uuid);
                });
    }


    @Override
    public void validateNumberOfSubWallets(int size) {
        log.info("Validating number of SubWallets: {}", size);
        if (size <= 19) {
            log.info("Validation passed. Current subwallet count: {}", size);
        } else {
            log.error("Validation failed. Maximum allowed subwallets: 19, provided: {}", size);
            throw new MaxSubWalletsExceededException("Maximum 19 sub wallets are allowed.");
        }
    }

    @Override
    public Page<Transaction> validateTransactionsByUuidAndSubWalletId(
            String uuid, String walletId,String transactionGroupId, TransactionType transactionType, Pageable pageable) {

        if (transactionGroupId!=null){
          return transactionRepository.findByUser_UuidAndGroupId(uuid,transactionGroupId,pageable);
        }

        Page<Transaction> transactions;

        if (walletId != null) {
            // Subwallet is specified
            if (transactionType == TransactionType.DEBIT) {
                // Only those where subWalletId is the FROM wallet
                transactions = transactionRepository.findByUser_UuidAndFromWalletIdAndTransactionType(
                        uuid, walletId, TransactionType.DEBIT, pageable);
            } else if (transactionType == TransactionType.CREDIT) {
                // Only those where subWalletId is the TO wallet
                transactions = transactionRepository.findByUser_UuidAndToWalletIdAndTransactionType(
                        uuid, walletId, TransactionType.CREDIT, pageable);
            } else {
                transactions = transactionRepository.findAllByUserAndWallet(
                        uuid, walletId, pageable);
            }
        } else {
            // No subwallet filter
            if (transactionType != null) {
                transactions = transactionRepository.findByUser_UuidAndTransactionTypeAndIsMasterFalse(uuid, transactionType, pageable);
            } else {
                transactions = transactionRepository.findByUser_UuidAndIsMasterFalse(uuid, pageable);
            }
        }

        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException("No transactions found for User UUID: " + uuid);
        }

        return transactions;
    }


//
//    @Override
//    public Page<Transaction> validateTransactionsByUuidAndSubWalletId(String uuid, String subWalletId, Pageable pageable) {
//        if (subWalletId != null) {
//            log.info("Fetching transactions for User UUID: {} and SubWallet ID: {}", uuid, subWalletId);
//            Page<Transaction> transactions = transactionRepository.findByUser_UuidAndFromWalletId(uuid, subWalletId, pageable);
//
//            if (transactions.isEmpty()) {
//                log.error("No transactions found for User UUID: {} and SubWallet ID: {}. Possible invalid ID(s).", uuid, subWalletId);
//                throw new TransactionNotFoundException(
//                        "No transactions found for the given SubWallet ID: " + subWalletId + " under User UUID: " + uuid);
//            }
//            return transactions;
//        }
//        log.info("Fetching transactions for User UUID: {}", uuid);
//        Page<Transaction> transactions = transactionRepository.findByUser_UuidAndIsMasterFalse(uuid, pageable);
//        if (transactions.isEmpty()) {
//            log.error("No transactions found for User UUID: {}. Possible invalid UUID.", uuid);
//            throw new TransactionNotFoundException(
//                    "No transactions found for the given User UUID: " + uuid);
//        }
//        log.debug("Found {} transactions for User UUID: {}", transactions.getTotalElements(), uuid);
//        return transactions;
//
//    }

    @Override
    public void validateBalance(Double balance, Double amount) {
        log.info("Validating transaction with balance: {} and amount: {}", balance, amount);
        if (balance == null || amount == null) {
            log.error("Validation failed: Balance or amount is null. Balance={}, Amount={}", balance, amount);
            throw new NullAmountException("Balance and amount must not be null. Provided balance=" + balance + ", amount=" + amount);
        }
        if (balance < amount) {
            log.error("Validation failed: Insufficient balance. Available={}, Required={}", balance, amount);
            throw new InsufficientBalanceException("Insufficient funds: required=" + amount + ", available=" + balance);
        }
        log.debug("Validation successful: Transaction can proceed. Balance={}, Amount={}", balance, amount);
    }


    @Override
    public SubWallet findSubWalletIfExists(MainWallet wallet, String subWalletId) {
        if (wallet == null || subWalletId == null) {
            log.error("Validation failed: wallet or subWalletId is null. Wallet={}, SubWalletId={}", wallet.getMainWalletId(), subWalletId);
            throw new NullValueException("wallet or subWalletId must not be null.");
        }
        log.info("Searching for SubWallet with ID: {} in MainWallet: {}", subWalletId, wallet.getMainWalletId());
        return wallet.getSubWallets().stream()
                .filter(sw -> sw.getSubWalletId().equals(subWalletId))
                .findFirst()
                .orElseGet(() -> {
                    log.warn("SubWallet not found. SubWalletId={} under MainWallet={}", subWalletId, wallet.getMainWalletId());
                    return null;
                });
    }

    @Override
    public Boolean removeSubwallet(MainWallet wallet, String subWalletId) {
        return wallet.getSubWallets().removeIf(subwallet->subwallet.getSubWalletId().equals(subWalletId));
    }

    @Override
    public MasterWallet getMasterWalletInfo(String uuid) {
        log.info("Fetching MasterWallet for User UUID: {}", uuid);
        return masterWalletRepository.findByUser_Uuid(uuid).orElseThrow(() -> {
            log.error("User not found with ID: {}", uuid);
            return new UserNotFoundException("User not found with ID: " + uuid);
        });
    }


}

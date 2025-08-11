package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.exception.*;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.MasterWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import javax.naming.LimitExceededException;
@Log4j2
@Component
public class ValidationsImpl implements Validations{
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
        return userRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException("User not found with UUID: " + uuid));
    }

    public MainWallet getMainWalletInfo(String uuid) {
        return mainWalletRepository.findByUser_Uuid(uuid)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + uuid));
    }

    public SubWallet validateSubWalletExists(MainWallet wallet, String subWalletId) {
        return wallet.getSubWallets().stream()
                .filter(sw -> sw.getSubWalletId().equals(subWalletId))
                .findFirst()
                .orElseThrow(() -> new WalletNotFoundException("SubWallet not found with ID: " + subWalletId));
    }

    @Override
    public boolean validateNumberOfSubWallets(int size) {
        if (size<=19){
            return true;
        }
        else throw new RuntimeException("Maximum 19 sub wallets are allowed.");
    }

    @Override
    public Page<Transaction> validateTransactionsBySubWalletId(String uuid, String subWalletId, Pageable pageable) {
        if (subWalletId != null) {
            Page<Transaction> transactions = transactionRepository.findByUser_UuidAndSubWalletId(uuid, subWalletId, pageable);

            if (transactions.isEmpty()) {
                log.error("No transactions found for SubWallet ID: {} , provided id might not be valid.", subWalletId);
                throw new TransactionNotFoundException("Please provide a valid Id. Either Uuid or subWallet Id is invalid!");
            }
            return transactions;
        }
        Page<Transaction> transactions = transactionRepository.findByUser_Uuid(uuid, pageable);
        if (transactions.isEmpty()) {
            log.error("No transactions found for user with uuid: {} , provided id might not be valid.", uuid);
            throw new TransactionNotFoundException("Please provide a valid user uuid!");
        }
        return transactions;

    }

    public void checkMainWalletAmount(MainWallet mainWallet, SubWallet subWallet, Double amount) {
        if (mainWallet == null || subWallet == null) {
            throw new ResourceNotFoundException("Source or Target SubWallet cannot be null");
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (mainWallet.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient funds in mainWallet SubWallet");
        }

         }

    @Override
    public void checkSubWalletAmount( SubWallet subWallet, Double amount) {

        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (subWallet.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient funds in "+subWallet.getSubWalletName()+" SubWallet");
        }

    }
    public SubWallet findSubWalletIfExists(MainWallet wallet, String subWalletId) {
        if (wallet == null || subWalletId == null) return null;
        return wallet.getSubWallets().stream()
                .filter(sw -> sw.getSubWalletId().equals(subWalletId))
                .findFirst()
                .orElse(null);
}
public MasterWallet getMasterWalletInfo(String uuid) {
    return masterWalletRepository.findByUser_Uuid(uuid).orElseThrow(() -> {
        log.error("User not found with ID: {}", uuid);
        return new UserNotFoundException("User not found with ID: " + uuid);
    });

    }
}


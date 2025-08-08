package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.exception.*;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidationsImpl implements Validations{
    private final UserRepository userRepository;
    private final MainWalletRepository mainWalletRepository;
    public ValidationsImpl(UserRepository userRepository, MainWalletRepository mainWalletRepository) {
        this.userRepository = userRepository;
        this.mainWalletRepository = mainWalletRepository;
    }


    public User validateUserExists(String uuid) {
        return userRepository.findById(uuid)
                .orElseThrow(() -> new UserNotFoundException("User not found with UUID: " + uuid));
    }

    public MainWallet validateWalletExistsForUser(String uuid) {
        return mainWalletRepository.findByUser_Uuid(uuid)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user: " + uuid));
    }

    public SubWallet validateSubWalletExists(MainWallet wallet, String subWalletId) {
        return wallet.getSubWallets().stream()
                .filter(sw -> sw.getSubWalletId().equals(subWalletId))
                .findFirst()
                .orElseThrow(() -> new WalletNotFoundException("SubWallet not found with ID: " + subWalletId));
    }



    public void validateTransfer(SubWallet sourceWalletId, SubWallet targetWalletId, Double amount) {
        if (sourceWalletId == null || targetWalletId == null) {
            throw new ResourceNotFoundException("Source or Target SubWallet cannot be null");
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (sourceWalletId.getBalance() < amount) {
            throw new InsufficientBalanceException("Insufficient funds in sourceWalletId SubWallet");
        }

        if (sourceWalletId.getSubWalletId().equals(targetWalletId.getSubWalletId())) {
            throw new IllegalArgumentException("Source and Target SubWallets cannot be the same");
        }
    }
}

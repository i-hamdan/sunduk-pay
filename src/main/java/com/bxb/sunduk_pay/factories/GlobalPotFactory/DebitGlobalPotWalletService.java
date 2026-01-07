package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.exception.InactiveGlobalWalletException;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalWallet;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class DebitGlobalPotWalletService implements GlobalPotOperation{

    /** validations class for subWallet.*/
    private final Validations validations;

    /**
     * Validations for global pot operations.
     */
    private final GlobalPotValidations globalPotValidations;


    private final GlobalWalletRepository globalWalletRepository;

    private final TransactionRepository transactionRepository;



    @Override
    public GlobalPotRequestType getGlobalPotRequestType(){
        return GlobalPotRequestType.DEBIT_GLOBAL_POT_WALLET;
    }

    @Override
    @Transactional
    public GlobalPotResponse perform(GlobalPotRequest request) throws IOException{

        User admin = validations.getUserInfo(request.getAdminUuid());
        globalPotValidations.validateAdmin(admin);

        GlobalPot globalPot = globalPotValidations.getGlobalPot
                (request.getGlobalPotId());

        GlobalWallet globalWallet = globalPot.getGlobalWallet();

        if (!globalWallet.getIsActive()) {
            throw new InactiveGlobalWalletException("Global wallet is inactive");
        }

        Double amount = request.getTargetAmount();

        validations.validateTargetBalance(globalWallet.getBalance(), amount);

        globalWallet.setBalance( globalWallet.getBalance()-amount);
        globalWalletRepository.save(globalWallet);

        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .user(admin)
                .amount(amount)
                .transactionType(TransactionType.DEBIT)
                .transactionLevel(TransactionLevel.GLOBAL_POT)
                .dateTime(LocalDateTime.now())
                .description("Admin debited amount-" +amount+
                        " from Global Pot")
                .fromWallet("GlobalPotWallet")
                .fromWalletId(globalWallet.getGlobalWalletId())
                .toWallet("External")
                .isInvestment(false)
                .isMaster(false)
                .build();

        transactionRepository.save(transaction);

        return GlobalPotResponse.builder()
                .message("Amount-" +amount+ " debited from " +
                        globalWallet.getGlobalWalletId())
                .currentBalance(globalWallet.getBalance())
                .status("SUCCESS")
                .build();
    }


}

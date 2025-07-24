package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.TransactionMapperImpl;
import com.bxb.sunduk_pay.exception.InsufficientBalanceException;
import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.exception.TransactionNotFoundException;
import com.bxb.sunduk_pay.exception.WalletNotFoundException;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.Wallet;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.repository.WalletRepository;
import com.bxb.sunduk_pay.request.TransactionRequest;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.service.TransactionService;
import com.bxb.sunduk_pay.util.TransactionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final TransactionMapperImpl transactionMapper;

    public TransactionServiceImpl(TransactionRepository repository, WalletRepository walletRepository, TransactionMapperImpl transactionMapper) {

        this.transactionRepository = repository;
        this.walletRepository = walletRepository;
        this.transactionMapper = transactionMapper;
    }

    @Transactional
    public String createTransaction(TransactionRequest request) {
        Wallet userWallet = walletRepository.findById(request.getWalletId()).orElseThrow(() -> new WalletNotFoundException("Invalid wallet Id! PLease provide a valid wallet Id."));

        if (userWallet.getIsDeleted()) {
            throw new WalletNotFoundException("This wallet is already deleted.");
        }

        if (request.getAmount() == null || request.getAmount() == 0) {
            throw new ResourceNotFoundException("Amount cannot be null or zero!");
        }

        Transaction userTransaction=transactionMapper.toTransaction(request);
        userTransaction.setTransactionId(UUID.randomUUID().toString());
        userTransaction.setDateTime(LocalDateTime.now());
        userTransaction.setIsDeleted(false);
        userTransaction.setWallet(userWallet);

        if (userTransaction.getTransactionType() == TransactionType.CREDIT) {
            userWallet.setBalance(userWallet.getBalance() + userTransaction.getAmount());
        }
        if (userTransaction.getTransactionType() == TransactionType.DEBIT) {
            if (userWallet.getBalance() < userTransaction.getAmount()) {
                throw new InsufficientBalanceException("Insufficient balance in your wallet with wallet id:" + userWallet.getWalletId() + ".");
            }
            userWallet.setBalance(userWallet.getBalance() - userTransaction.getAmount());
        }

        userWallet.getTransactionHistory().add(userTransaction);

        transactionRepository.save(userTransaction);
        walletRepository.save(userWallet);
        return "Transaction successful.";
    }

    @Override
    public TransactionResponse getInfoById(String id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException("The provided id is invalid!"));
        if (transaction.getIsDeleted()) {
            throw new TransactionNotFoundException("This transaction is already deleted.");
        }
        return transactionMapper.toTransactionResponse(transaction);
    }


}


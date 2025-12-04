package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TransactionRepositoryTest {

    @Autowired
    TransactionRepository transactionRepository;

    @Test
    void run(){
        for (Transaction transaction : transactionRepository.findAllByUserUuidAndWalletId("b56555bc-6621-461a-aee8-f76bbd90177e", "3ee06df5-c3da-4aa9-a500-9763f2e936f3")) {
            System.out.println(transaction.getRemainingBalance());
        }

    }
}
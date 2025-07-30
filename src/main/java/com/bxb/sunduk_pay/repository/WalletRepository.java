package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.Wallet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends MongoRepository<Wallet, String> {
    Optional<Wallet> findByUser_Uuid(String uuid);
    Wallet findByWalletId(String walletId);
}

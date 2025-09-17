package com.bxb.sunduk_pay.repository;


import com.bxb.sunduk_pay.model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction,String> {
List<Transaction> findByWallet_walletId(String walletId, Pageable pageable);
List<Transaction> findByWallet_walletId(String walletId, Sort sort);

    List<Transaction>findByWallet_walletId(String walletId);
}

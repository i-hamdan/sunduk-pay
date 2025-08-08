package com.bxb.sunduk_pay.repository;


import com.bxb.sunduk_pay.model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction,String> {

List<Transaction>findByMainWallet_mainWalletIdAndUser_Uuid(String walletId,String uuid);



}

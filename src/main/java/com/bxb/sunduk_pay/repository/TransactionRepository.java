package com.bxb.sunduk_pay.repository;


import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.util.TransactionType;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface
TransactionRepository extends MongoRepository<Transaction,String> {

    List<Transaction> findByMainWallet_mainWalletIdAndUser_Uuid(String walletId, String uuid);


    Page<Transaction> findByUser_UuidAndIsMasterFalse(String uuid, Pageable pageable);


    // Debit transactions of a subwallet
    Page<Transaction> findByUser_UuidAndFromWalletIdAndTransactionType(
            String uuid, String fromWalletId, TransactionType transactionType, Pageable pageable);

    // Credit transactions of a subwallet
    Page<Transaction> findByUser_UuidAndToWalletIdAndTransactionType(
            String uuid, String toWalletId, TransactionType transactionType, Pageable pageable);

    @Query("{ 'user.uuid': ?0, $or: [ " +
            "{ $and: [ { 'fromWalletId': ?1 }, { 'transactionType': 'DEBIT' } ] }, " +
            "{ $and: [ { 'toWalletId': ?1 }, { 'transactionType': 'CREDIT' } ] } " +
            "] }")
    Page<Transaction> findAllByUserAndWallet(String uuid, String walletId, Pageable pageable);

    @Query("{ 'user.uuid': ?0, $or: [ " +
            "{ $and: [ { 'fromWalletId': ?1 }, { 'transactionType': 'DEBIT' } ] }, " +
            "{ $and: [ { 'toWalletId': ?1 }, { 'transactionType': 'CREDIT' } ] } " +
            "] }")
    List<Transaction> findAllByUserAndWallet(String uuid, String walletId);

    Page<Transaction> findByUser_UuidAndTransactionTypeAndIsMasterFalse(String uuid, TransactionType transactionType, Pageable pageable);

    Page<Transaction> findByUser_UuidAndGroupId(String uuid,String groupId,Pageable pageable);
}
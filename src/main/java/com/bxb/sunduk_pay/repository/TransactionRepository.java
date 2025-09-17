package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    List<Transaction> findByMainWallet_mainWalletIdAndUser_Uuid(String walletId, String uuid);

    Page<Transaction> findByUser_UuidAndIsMasterFalse(String uuid, Pageable pageable);

    // Debit transactions of a subwallet
    Page<Transaction> findByUser_UuidAndFromWalletIdAndTransactionType(
            String uuid, String fromWalletId, TransactionType transactionType, Pageable pageable);

    // Debit transactions of a subwallet + method
    Page<Transaction> findByUser_UuidAndFromWalletIdAndTransactionTypeAndPaymentMethod(
            String uuid, String fromWalletId, TransactionType transactionType, PaymentMethod paymentMethod, Pageable pageable);

    // Credit transactions of a subwallet
    Page<Transaction> findByUser_UuidAndToWalletIdAndTransactionType(
            String uuid, String toWalletId, TransactionType transactionType, Pageable pageable);

    // Credit transactions of a subwallet + method
    Page<Transaction> findByUser_UuidAndToWalletIdAndTransactionTypeAndPaymentMethod(
            String uuid, String toWalletId, TransactionType transactionType, PaymentMethod paymentMethod, Pageable pageable);

    // All subwallet transactions
    @Query("{ 'user.uuid': ?0, $or: [ " +
            "{ $and: [ { 'fromWalletId': ?1 }, { 'transactionType': 'DEBIT' } ] }, " +
            "{ $and: [ { 'toWalletId': ?1 }, { 'transactionType': 'CREDIT' } ] } " +
            "] }")
    Page<Transaction> findAllByUserAndWallet(String uuid, String walletId, Pageable pageable);

    // All subwallet transactions + method
    @Query("{ 'user.uuid': ?0, $or: [ " +
            "{ $and: [ { 'fromWalletId': ?1 }, { 'transactionType': 'DEBIT' } ] }, " +
            "{ $and: [ { 'toWalletId': ?1 }, { 'transactionType': 'CREDIT' } ] } " +
            "], 'paymentMethod': ?2 }")
    Page<Transaction> findByUser_UuidAndWalletIdAndPaymentMethod(
            String uuid, String walletId, PaymentMethod paymentMethod, Pageable pageable);

    // (Optional, if you still need list form elsewhere)
    @Query("{ 'user.uuid': ?0, $or: [ " +
            "{ $and: [ { 'fromWalletId': ?1 }, { 'transactionType': 'DEBIT' } ] }, " +
            "{ $and: [ { 'toWalletId': ?1 }, { 'transactionType': 'CREDIT' } ] } " +
            "] }")
    List<Transaction> findAllByUserAndWallet(String uuid, String walletId);

    // By type only
    Page<Transaction> findByUser_UuidAndTransactionTypeAndIsMasterFalse(
            String uuid, TransactionType transactionType, Pageable pageable);

    // By type + method
    Page<Transaction> findByUser_UuidAndTransactionTypeAndPaymentMethodAndIsMasterFalse(
            String uuid, TransactionType transactionType, PaymentMethod paymentMethod, Pageable pageable);

    // By group
    Page<Transaction> findByUser_UuidAndGroupId(String uuid, String groupId, Pageable pageable);

    // By method only + isMasterFalse
    Page<Transaction> findByUser_UuidAndPaymentMethodAndIsMasterFalse(
            String uuid, PaymentMethod paymentMethod, Pageable pageable);
}

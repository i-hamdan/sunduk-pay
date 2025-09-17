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

/**
 * Repository interface for performing CRUD operations and custom queries
 * on {@link Transaction} documents in MongoDB.
 * <p>
 * Provides predefined methods for fetching transactions by wallet, user,
 * transaction type, payment method, and grouping.
 * </p>
 */
@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    /**
     * Finds all transactions by main wallet ID and user UUID.
     *
     * @param walletId main wallet ID
     * @param uuid     user UUID
     * @return list of matching transactions
     */
    List<Transaction> findByMainWallet_mainWalletIdAndUser_Uuid(String walletId, String uuid);

    /**
     * Finds all non-master transactions for a user with pagination.
     *
     * @param uuid     user UUID
     * @param pageable pagination information
     * @return page of transactions
     */
    Page<Transaction> findByUser_UuidAndIsMasterFalse(String uuid, Pageable pageable);

    /**
     * Finds debit transactions of a specific sub-wallet.
     */
    Page<Transaction> findByUser_UuidAndFromWalletIdAndTransactionType(
            String uuid, String fromWalletId, TransactionType transactionType, Pageable pageable);

    /**
     * Finds debit transactions of a specific sub-wallet filtered by payment method.
     */
    Page<Transaction> findByUser_UuidAndFromWalletIdAndTransactionTypeAndPaymentMethod(
            String uuid, String fromWalletId, TransactionType transactionType,
            PaymentMethod paymentMethod, Pageable pageable);

    /**
     * Finds credit transactions of a specific sub-wallet.
     */
    Page<Transaction> findByUser_UuidAndToWalletIdAndTransactionType(
            String uuid, String toWalletId, TransactionType transactionType, Pageable pageable);

    /**
     * Finds credit transactions of a specific sub-wallet filtered by payment method.
     */
    Page<Transaction> findByUser_UuidAndToWalletIdAndTransactionTypeAndPaymentMethod(
            String uuid, String toWalletId, TransactionType transactionType,
            PaymentMethod paymentMethod, Pageable pageable);

    /**
     * Finds all sub-wallet transactions (both debit and credit) for a user.
     */
    @Query("{ 'user.uuid': ?0, $or: [ " +
            "{ $and: [ { 'fromWalletId': ?1 }, { 'transactionType': 'DEBIT' } ] }, " +
            "{ $and: [ { 'toWalletId': ?1 }, { 'transactionType': 'CREDIT' } ] } " +
            "] }")
    Page<Transaction> findAllByUserAndWallet(String uuid, String walletId, Pageable pageable);

    /**
     * Finds all sub-wallet transactions (debit and credit) for a user,
     * filtered by payment method.
     */
    @Query("{ 'user.uuid': ?0, $or: [ " +
            "{ $and: [ { 'fromWalletId': ?1 }, { 'transactionType': 'DEBIT' } ] }, " +
            "{ $and: [ { 'toWalletId': ?1 }, { 'transactionType': 'CREDIT' } ] } " +
            "], 'paymentMethod': ?2 }")
    Page<Transaction> findByUser_UuidAndWalletIdAndPaymentMethod(
            String uuid, String walletId, PaymentMethod paymentMethod, Pageable pageable);

    /**
     * Finds all sub-wallet transactions (debit and credit) as a list.
     */
    @Query("{ 'user.uuid': ?0, $or: [ " +
            "{ $and: [ { 'fromWalletId': ?1 }, { 'transactionType': 'DEBIT' } ] }, " +
            "{ $and: [ { 'toWalletId': ?1 }, { 'transactionType': 'CREDIT' } ] } " +
            "] }")
    List<Transaction> findAllByUserAndWallet(String uuid, String walletId);

    /**
     * Finds transactions by type (credit or debit) excluding master transactions.
     */
    Page<Transaction> findByUser_UuidAndTransactionTypeAndIsMasterFalse(
            String uuid, TransactionType transactionType, Pageable pageable);

    /**
     * Finds transactions by type and payment method, excluding master transactions.
     */
    Page<Transaction> findByUser_UuidAndTransactionTypeAndPaymentMethodAndIsMasterFalse(
            String uuid, TransactionType transactionType,
            PaymentMethod paymentMethod, Pageable pageable);

    /**
     * Finds transactions belonging to a specific group ID.
     */
    Page<Transaction> findByUser_UuidAndGroupId(String uuid, String groupId, Pageable pageable);

    /**
     * Finds transactions by payment method only, excluding master transactions.
     */
    Page<Transaction> findByUser_UuidAndPaymentMethodAndIsMasterFalse(
            String uuid, PaymentMethod paymentMethod, Pageable pageable);
}

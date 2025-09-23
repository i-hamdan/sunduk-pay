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
 * Repository interface for managing Transaction entities in MongoDB.
 */
@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {
/**     * Finds transactions by main wallet ID and user UUID.
     *
     * @param walletId the main wallet ID
     * @param uuid     the user UUID
     * @return list of transactions
     */
// CHECKSTYLE:OFF
    List<Transaction> findByMainWallet_mainWalletIdAndUser_Uuid(
            String walletId,
            String uuid);
// CHECKSTYLE:ON

    /*** Finds transactions by user UUID.
 * And isMaster flag set to false.
     * @param uuid the user UUID
     * @return list of transactions
     */
    // CHECKSTYLE:OFF
    Page<Transaction> findByUser_UuidAndIsMasterFalse(
            String uuid,
            Pageable pageable);
// CHECKSTYLE:ON

    /**find transactions by user UUID,fromId and transaction type.
     *
     * @param uuid the user UUID
     * @param fromWalletId from wallet id
     * @param transactionType the type of transaction
     * @return list of transactions
     */
    // CHECKSTYLE:OFF
    Page<Transaction> findByUser_UuidAndFromWalletIdAndTransactionType(
            String uuid,
            String fromWalletId,
            TransactionType transactionType,
            Pageable pageable);
// CHECKSTYLE:ON

    /**find transactions by user UUID,fromId,transaction type and payment method.
     *
     * @param uuid the user UUID
     * @param fromWalletId from wallet id
     * @param  transactionType the type of transaction
     * @param paymentMethod the method of payment
     * @return list of transactions
     */
    // CHECKSTYLE:OFF
    Page<Transaction> findByUser_UuidAndFromWalletIdAndTransactionTypeAndPaymentMethod(
            String uuid,
            String fromWalletId,
            TransactionType transactionType,
            PaymentMethod paymentMethod,
            Pageable pageable);
// CHECKSTYLE:ON
    /**find transactions by user UUID,toId and transaction type.
     *
     * @param uuid the user UUID
     * @param toWalletId to wallet id
     * @param  transactionType the type of transaction
     * @return list of transactions
     */
    // CHECKSTYLE:OFF
    Page<Transaction> findByUser_UuidAndToWalletIdAndTransactionType(
            String uuid,
            String toWalletId,
            TransactionType transactionType,
            Pageable pageable);
// CHECKSTYLE:ON

    /**find transactions by user UUID,toId,transaction type and payment method.
     *
     * @param uuid the user UUID
     * @param toWalletId to wallet id
     * @param  transactionType the type of transaction
     * @param paymentMethod the method of payment
     * @return list of transactions
     */
    // CHECKSTYLE:OFF
    Page<Transaction> findByUser_UuidAndToWalletIdAndTransactionTypeAndPaymentMethod(
            String uuid,
            String toWalletId,
            TransactionType transactionType,
            PaymentMethod paymentMethod,
            Pageable pageable);
// CHECKSTYLE:ON

    /**find transactions by user UUID,walletId and pageable.
     *
     * @param uuid the user UUID
     * @param walletId wallet id
     * @param pageable pagination information
     * @return list of transactions
     */

    // CHECKSTYLE:OFF
    @Query("{ 'user.uuid': ?0, $or: [ " +
            "{ $and: [ { 'fromWalletId': ?1 }, { 'transactionType': 'DEBIT' } ] }, " +
            "{ $and: [ { 'toWalletId': ?1 }, { 'transactionType': 'CREDIT' } ] } " +
            "] }")
    Page<Transaction> findAllByUserAndWallet(String uuid,
                                             String walletId,
                                             Pageable pageable);
// CHECKSTYLE:ON

    /**find transactions by user UUID,walletId,payment method and pageable.
     *
     * @param uuid the user UUID
     * @param walletId wallet id
     * @param paymentMethod the method of payment
     * @param pageable pagination information
     * @return list of transactions
     */
// CHECKSTYLE:OFF
    @Query("{ 'user.uuid': ?0, $or: [ " +
            "{ $and: [ { 'fromWalletId': ?1 }, { 'transactionType': 'DEBIT' } ] }, " +
            "{ $and: [ { 'toWalletId': ?1 }, { 'transactionType': 'CREDIT' } ] } " +
            "], 'paymentMethod': ?2 }")
    Page<Transaction> findByUser_UuidAndWalletIdAndPaymentMethod(
            String uuid,
            String walletId,
            PaymentMethod paymentMethod,
            Pageable pageable);
// CHECKSTYLE:ON

    /**find transactions by user UUID and walletId.
     *
     * @param uuid the user UUID
     * @param walletId wallet id
     * @return list of transactions
     */
// CHECKSTYLE:OFF
    @Query("{ 'user.uuid': ?0, $or: [ " +
            "{ $and: [ { 'fromWalletId': ?1 }, { 'transactionType': 'DEBIT' } ] }, " +
            "{ $and: [ { 'toWalletId': ?1 }, { 'transactionType': 'CREDIT' } ] } " +
            "] }")
    List<Transaction> findAllByUserAndWallet(String uuid,
                                             String walletId);
// CHECKSTYLE:ON
    /**find transactions by user UUID,transaction type and isMaster false.
     *
     * @param uuid the user UUID
     * @param transactionType the type of transaction
     * @param pageable pagination information
     * @return list of transactions
     */
// CHECKSTYLE:OFF
    Page<Transaction> findByUser_UuidAndTransactionTypeAndIsMasterFalse(
            String uuid,
            TransactionType transactionType,
            Pageable pageable);
// CHECKSTYLE:ON

    /**find transactions by user UUID,transaction type,payment method and isMaster false.
     * @param uuid the user UUID
     * @param transactionType the type of transaction
     * @param paymentMethod the method of payment
     * @param pageable pagination information
     * @return list of transactions
     */
   // CHECKSTYLE:OFF
    Page<Transaction> findByUser_UuidAndTransactionTypeAndPaymentMethodAndIsMasterFalse(
            String uuid,
            TransactionType transactionType,
            PaymentMethod paymentMethod,
            Pageable pageable);
// CHECKSTYLE:ON
    /**find transactions by user UUID,groupId and pageable.
     * @param uuid the user UUID
     * @param groupId transaction group id
     * @param pageable pagination information
        * @return list of transactions
        */
    // CHECKSTYLE:OFF
    Page<Transaction> findByUser_UuidAndGroupId(String uuid,
                                                String groupId,
                                                Pageable pageable);
// CHECKSTYLE:ON

    /**find transactions by user UUID,payment method and isMaster false.
     * @param uuid the user UUID
     * @param paymentMethod the method of payment
     * @param pageable pagination information
     * @return list of transactions
     */
// CHECKSTYLE:OFF
    Page<Transaction> findByUser_UuidAndPaymentMethodAndIsMasterFalse(
            String uuid,
            PaymentMethod paymentMethod,
            Pageable pageable);
// CHECKSTYLE:ON
}

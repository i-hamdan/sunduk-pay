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
 * TRANSACTION REPOSITORY.
 * Repository interface for managing
 * Transaction entities in MongoDB.
 */
@Repository
public interface TransactionRepository
        extends MongoRepository<Transaction, String> {
/**     * Finds transactions by main wallet ID and user UUID.
     *
     * @param walletId the main wallet ID
     * @param uuid     the user UUID
     * @return list of transactions
     */
    List<Transaction> findByMainWalletMainWalletIdAndUserUuid(
            String walletId,
            String uuid);

    /*** Finds transactions by user UUID.
 * And isMaster flag set to false.
     * @param uuid the user UUID
     * @param pageable pagination information
     * @return list of transactions
     */
    Page<Transaction> findByUserUuidAndIsMasterFalse(
            String uuid,
            Pageable pageable);

    /**find transactions by user UUID,fromId and transaction type.
     *
     * @param uuid the user UUID
     * @param fromWalletId from wallet id
     * @param transactionType the type of transaction
     * @param pageable pagination information
     * @return list of transactions
     */
    Page<Transaction>
    findByUserUuidAndFromWalletIdAndTransactionType(
            String uuid,
            String fromWalletId,
            TransactionType transactionType,
            Pageable pageable);

    /**Method for fetching transactions.
     * find transactions by user UUID,fromId
     * ,transaction type and payment method.
     *
     * @param uuid the user UUID
     * @param fromWalletId from wallet id
     * @param  transactionType the type of transaction
     * @param paymentMethod the method of payment
     * @param pageable pagination information
     * @return list of transactions
     */
    Page<Transaction>
    findByUserUuidAndFromWalletIdAndTransactionTypeAndPaymentMethod(
            String uuid,
            String fromWalletId,
            TransactionType transactionType,
            PaymentMethod paymentMethod,
            Pageable pageable);
    /**find transactions by user UUID,toId and transaction type.
     *
     * @param uuid the user UUID
     * @param toWalletId to wallet id
     * @param  transactionType the type of transaction
     * @param pageable pagination information
     * @return list of transactions
     */
    Page<Transaction>
    findByUserUuidAndToWalletIdAndTransactionType(
            String uuid,
            String toWalletId,
            TransactionType transactionType,
            Pageable pageable);

    /**find transactions by user UUID,toId,transaction type and payment method.
     *
     * @param uuid the user UUID
     * @param toWalletId to wallet id
     * @param  transactionType the type of transaction
     * @param paymentMethod the method of payment
     * @param pageable pagination information
     * @return list of transactions
     */
    Page<Transaction>
    findByUserUuidAndToWalletIdAndTransactionTypeAndPaymentMethod(
            String uuid,
            String toWalletId,
            TransactionType transactionType,
            PaymentMethod paymentMethod,
            Pageable pageable);

    /**find transactions by user UUID,walletId and pageable.
     *
     * @param uuid the user UUID
     * @param walletId wallet id
     * @param pageable pagination information
     * @return list of transactions
     */

    @Query("{ 'user.uuid': ?0, $or: [ "
      + "{ $and: [ { 'fromWalletId': ?1 }, { 'transactionType': 'DEBIT' } ] }, "
      + "{ $and: [ { 'toWalletId': ?1 }, { 'transactionType': 'CREDIT' } ] } "
      + "] }")
    Page<Transaction> findAllByUserAndWallet(
            String uuid,
            String walletId,
            Pageable pageable);

    /**Method for fetching transactions.
     * find transactions by user UUID,
     * walletId,payment method and pageable.
     * @param uuid the user UUID
     * @param walletId wallet id
     * @param paymentMethod the method of payment
     * @param pageable pagination information
     * @return list of transactions
     */

    @Query("{ 'user.uuid': ?0, $or: [ "
      + "{ $and: [ { 'fromWalletId': ?1 }, { 'transactionType': 'DEBIT' } ] }, "
      + "{ $and: [ { 'toWalletId': ?1 }, { 'transactionType': 'CREDIT' } ] } "
      + "], 'paymentMethod': ?2 }")
    Page<Transaction> findByUserUuidAndWalletIdAndPaymentMethod(
            String uuid,
            String walletId,
            PaymentMethod paymentMethod,
            Pageable pageable);

    /**find transactions by user UUID and walletId.
     *
     * @param uuid the user UUID
     * @param walletId wallet id
     * @return list of transactions
     */

    @Query("{ 'user.uuid': ?0, $or: [ "
      + "{ $and: [ { 'fromWalletId': ?1 }, { 'transactionType': 'DEBIT' } ] }, "
      + "{ $and: [ { 'toWalletId': ?1 }, { 'transactionType': 'CREDIT' } ] } "
      + "] }")
    List<Transaction> findAllByUserAndWallet(
            String uuid,
            String walletId);

    /**Method for fetching transactions.
     * find transactions by user UUID,
     * transaction type and isMaster false.
     *
     * @param uuid the user UUID
     * @param transactionType the type of transaction
     * @param pageable pagination information
     * @return list of transactions
     */


    Page<Transaction> findByUserUuidAndTransactionTypeAndIsMasterFalse(
            String uuid,
            TransactionType transactionType,
            Pageable pageable);


    /**Method for fetching transactions.
     * find transactions by user UUID,
     * transaction type,payment method and isMaster false.
     * @param uuid the user UUID
     * @param transactionType the type of transaction
     * @param paymentMethod the method of payment
     * @param pageable pagination information
     * @return list of transactions
     */
    Page<Transaction>
    findByUserUuidAndTransactionTypeAndPaymentMethodAndIsMasterFalse(
            String uuid,
            TransactionType transactionType,
            PaymentMethod paymentMethod,
            Pageable pageable);

    /**find transactions by user UUID,groupId and pageable.
     * @param uuid the user UUID
     * @param groupId transaction group id
     * @param pageable pagination information
        * @return list of transactions
        */
    Page<Transaction> findByUserUuidAndGroupId(
            String uuid,
            String groupId,
            Pageable pageable);


    /**find transactions by user UUID,
     * payment method and isMaster false.
     * @param uuid the user UUID
     * @param paymentMethod the method of payment
     * @param pageable pagination information
     * @return list of transactions
     */
    Page<Transaction> findByUserUuidAndPaymentMethodAndIsMasterFalse(
            String uuid,
            PaymentMethod paymentMethod,
            Pageable pageable);
}

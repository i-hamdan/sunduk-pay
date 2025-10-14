package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * TRANSACTION REPOSITORY.
 * Repository interface for managing
 * Transaction entities in MongoDB.
 */
public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    /*** Finds transactions by user UUID.
     * And isMaster flag set to false.
     * @param uuid the user UUID
     * @param pageable pagination information
     * @return list of transactions
     */
    Page<Transaction> findByUserUuidAndIsMasterFalse(
            Long uuid,
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
            Long uuid,
            Long fromWalletId,
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
            Long uuid,
            Long fromWalletId,
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
            Long uuid,
            Long toWalletId,
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
            Long uuid,
            Long toWalletId,
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

    @Query("""
    SELECT t FROM Transaction t
    WHERE t.user.uuid = :uuid
      AND (
        (t.fromWalletId = :walletId AND t.transactionType = com.bxb.sunduk_pay.util.TransactionType.DEBIT)
        OR (t.toWalletId = :walletId AND t.transactionType = com.bxb.sunduk_pay.util.TransactionType.CREDIT)
      )
""")
    Page<Transaction> findAllByUserUuidAndWalletId(
            @Param("uuid") Long uuid,
            @Param("walletId") Long walletId,
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

    @Query("""
    SELECT t FROM Transaction t
    WHERE t.user.uuid = :uuid
      AND t.paymentMethod = :paymentMethod
      AND (
        (t.fromWalletId = :walletId AND t.transactionType = com.bxb.sunduk_pay.util.TransactionType.DEBIT)
        OR (t.toWalletId = :walletId AND t.transactionType = com.bxb.sunduk_pay.util.TransactionType.CREDIT)
      )
""")
    Page<Transaction> findByUuidAndWalletIdAndPaymentMethod(
            @Param("uuid") Long uuid,
            @Param("walletId") Long walletId,
            @Param("paymentMethod") PaymentMethod paymentMethod,
            Pageable pageable);

    /**find transactions by user UUID and walletId.
     *
     * @param uuid the user UUID
     * @param walletId wallet id
     * @return list of transactions
     */

    @Query("""
    SELECT t FROM Transaction t
    WHERE t.user.uuid = :uuid
      AND (
        (t.fromWalletId = :walletId AND t.transactionType = com.bxb.sunduk_pay.util.TransactionType.DEBIT)
        OR (t.toWalletId = :walletId AND t.transactionType = com.bxb.sunduk_pay.util.TransactionType.CREDIT)
      )
""")
    List<Transaction> findAllByUserUuidAndWalletId(
            @Param("uuid") Long uuid,
            @Param("walletId") Long walletId);

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
            Long uuid,
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
            Long uuid,
            TransactionType transactionType,
            PaymentMethod paymentMethod,
            Pageable pageable);



    /**find transactions by user UUID,
     * payment method and isMaster false.
     * @param uuid the user UUID
     * @param paymentMethod the method of payment
     * @param pageable pagination information
     * @return list of transactions
     */
    Page<Transaction> findByUserUuidAndPaymentMethodAndIsMasterFalse(
            Long uuid,
            PaymentMethod paymentMethod,
            Pageable pageable);
}
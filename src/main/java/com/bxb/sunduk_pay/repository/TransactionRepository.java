package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * TRANSACTION REPOSITORY.
 * Repository interface for managing
 * Transaction entities in MongoDB.
 */
@Repository
public interface TransactionRepository
        extends JpaRepository<Transaction, String> {

    /*** Finds transactions by user UUID.
     * And isMaster flag set to false.
     * @param uuid the user UUID
     * @param pageable pagination information
     * @return list of transactions
     */
    Page<Transaction> findByUserUuidAndIsMasterFalse(
            String uuid,
            Pageable pageable);

    /**
     * find transactions by user UUID,fromId and transaction type.
     *
     * @param uuid            the user UUID
     * @param fromWalletId    from wallet id
     * @param transactionType the type of transaction
     * @param pageable        pagination information
     * @return list of transactions
     */
    Page<Transaction>
    findByUserUuidAndFromWalletIdAndTransactionType(
            String uuid,
            String fromWalletId,
            TransactionType transactionType,
            Pageable pageable);

    /**
     * Method for fetching transactions.
     * find transactions by user UUID,fromId
     * ,transaction type and payment method.
     *
     * @param uuid            the user UUID
     * @param fromWalletId    from wallet id
     * @param transactionType the type of transaction
     * @param paymentMethod   the method of payment
     * @param pageable        pagination information
     * @return list of transactions
     */
    Page<Transaction>
    findByUserUuidAndFromWalletIdAndTransactionTypeAndPaymentMethod(
            String uuid,
            String fromWalletId,
            TransactionType transactionType,
            PaymentMethod paymentMethod,
            Pageable pageable);

    /**
     * find transactions by user UUID,toId and transaction type.
     *
     * @param uuid            the user UUID
     * @param toWalletId      to wallet id
     * @param transactionType the type of transaction
     * @param pageable        pagination information
     * @return list of transactions
     */
    Page<Transaction>
    findByUserUuidAndToWalletIdAndTransactionType(
            String uuid,
            String toWalletId,
            TransactionType transactionType,
            Pageable pageable);

    /**
     * find transactions by user UUID,toId,transaction type and payment method.
     *
     * @param uuid            the user UUID
     * @param toWalletId      to wallet id
     * @param transactionType the type of transaction
     * @param paymentMethod   the method of payment
     * @param pageable        pagination information
     * @return list of transactions
     */
    Page<Transaction>
    findByUserUuidAndToWalletIdAndTransactionTypeAndPaymentMethod(
            String uuid,
            String toWalletId,
            TransactionType transactionType,
            PaymentMethod paymentMethod,
            Pageable pageable);

    /**
     * find transactions by user UUID,walletId and pageable.
     *
     * @param uuid     the user UUID
     * @param walletId wallet id
     * @param pageable pagination information
     * @return list of transactions
     */

    @Query("""
                SELECT t FROM Transaction t
                WHERE t.user.uuid = :uuid
                  AND (
                    (t.fromWalletId = :walletId AND t.transactionType =
                     com.bxb.sunduk_pay.util.TransactionType.DEBIT)
                    OR (t.toWalletId = :walletId AND t.transactionType =
                     com.bxb.sunduk_pay.util.TransactionType.CREDIT)
                  )
            """)
    Page<Transaction> findAllByUserUuidAndWalletId(
            @Param("uuid") String uuid,
            @Param("walletId") String walletId,
            Pageable pageable);


    /**
     * Method for fetching transactions.
     * find transactions by user UUID,
     * walletId,payment method and pageable.
     *
     * @param uuid          the user UUID
     * @param walletId      wallet id
     * @param paymentMethod the method of payment
     * @param pageable      pagination information
     * @return list of transactions
     */

    @Query("""
                SELECT t FROM Transaction t
                WHERE t.user.uuid = :uuid
                  AND t.paymentMethod = :paymentMethod
                  AND (
                    (t.fromWalletId = :walletId AND t.transactionType =
                     com.bxb.sunduk_pay.util.TransactionType.DEBIT)
                    OR (t.toWalletId = :walletId AND t.transactionType =
                     com.bxb.sunduk_pay.util.TransactionType.CREDIT)
                  )
            """)
    Page<Transaction> findByUuidAndWalletIdAndPaymentMethod(
            @Param("uuid") String uuid,
            @Param("walletId") String walletId,
            @Param("paymentMethod") PaymentMethod paymentMethod,
            Pageable pageable);

    /**
     * find transactions by user UUID and walletId.
     *
     * @param uuid     the user UUID
     * @param walletId wallet id
     * @return list of transactions
     */

    @Query("""
        SELECT t FROM Transaction t
        WHERE t.user.uuid = :uuid
          AND t.isInvestment = false
          AND (
                (t.fromWalletId = :walletId
                    AND t.transactionType =
                    com.bxb.sunduk_pay.util.TransactionType.DEBIT)
             OR (t.toWalletId = :walletId
                    AND t.transactionType =
                    com.bxb.sunduk_pay.util.TransactionType.CREDIT)
          )
    """)
    List<Transaction> findAllByUserUuidAndWalletId(
            @Param("uuid") String uuid,
            @Param("walletId") String walletId);

    /**
     * Method for fetching transactions.
     * find transactions by user UUID,
     * transaction type and isMaster false.
     *
     * @param uuid            the user UUID
     * @param transactionType the type of transaction
     * @param pageable        pagination information
     * @return list of transactions
     */
    Page<Transaction> findByUserUuidAndTransactionTypeAndIsMasterFalse(
            String uuid,
            TransactionType transactionType,
            Pageable pageable);


    /**
     * Method for fetching transactions.
     * find transactions by user UUID,
     * transaction type,payment method and isMaster false.
     *
     * @param uuid            the user UUID
     * @param transactionType the type of transaction
     * @param paymentMethod   the method of payment
     * @param pageable        pagination information
     * @return list of transactions
     */
    Page<Transaction>
    findByUserUuidAndTransactionTypeAndPaymentMethodAndIsMasterFalse(
            String uuid,
            TransactionType transactionType,
            PaymentMethod paymentMethod,
            Pageable pageable);


    /**
     * find transactions by user UUID,
     * payment method and isMaster false.
     *
     * @param uuid          the user UUID
     * @param paymentMethod the method of payment
     * @param pageable      pagination information
     * @return list of transactions
     */
    Page<Transaction> findByUserUuidAndPaymentMethodAndIsMasterFalse(
            String uuid,
            PaymentMethod paymentMethod,
            Pageable pageable);

    /**
     * find transactions by sender and receiver phoneNumbers,
     * payment method and isMaster false.
     *
     * @param phone1        the phoneNumber of sender
     * @param phone2        the phoneNumber of receiver
     * @param paymentMethod the method of payment
     * @return list of transactions
     */
    @Query("""
    SELECT t FROM Transaction t
    WHERE (
        (t.fromPhoneNumber = :phone1 AND t.toPhoneNumber = :phone2)
        OR (t.fromPhoneNumber = :phone2 AND t.toPhoneNumber = :phone1)
    )
    AND t.paymentMethod = :paymentMethod
    AND t.isMaster = false
    ORDER BY t.dateTime DESC
""")
    List<Transaction> findChatTransactions(
            @Param("phone1") String phone1,
            @Param("phone2") String phone2,
            @Param("paymentMethod") PaymentMethod paymentMethod
    );



    /**
     * find transactions by user UUID,
     * sender and receiver phoneNumbers,
     * payment method and isMaster false.
     *
     * @param uuid          the user UUID
     * @param phone1        the phoneNumber of sender
     * @param phone2        the phoneNumber of receiver
     * @param paymentMethod the method of payment
     * @param pageable      pagination information
     * @return list of transactions
     */
    @Query("""
    SELECT t FROM Transaction t
    WHERE
        t.user.uuid = :uuid
        AND (
            (t.fromPhoneNumber = :phone1 AND t.toPhoneNumber = :phone2)
            OR (t.fromPhoneNumber = :phone2 AND t.toPhoneNumber = :phone1)
        )
        AND t.paymentMethod = :paymentMethod
        AND t.isMaster = false
    ORDER BY t.dateTime DESC
""")

    Page<Transaction> getTransactionsBySenderAndReceiverId(
            @Param("uuid") String uuid,
            @Param("phone1") String phone1,
            @Param("phone2") String phone2,
            @Param("paymentMethod") PaymentMethod paymentMethod,
            Pageable pageable
    );


    /**
     * Method for fetching transactions.
     * find transactions by recipient UPI ID
     * and isMaster false.
     *
     * @param recipientUpiId recipient UPI ID
     * @param pageable       pagination information
     * @return list of transactions
     */
    Page<Transaction> findByRecipientUpiIdAndIsMasterFalse(
            String recipientUpiId,
            Pageable pageable);

    /**
     * Method for fetching transactions.
     * find transactions by transaction level,
     * to global pot ID and isMaster false.
     * @param transactionLevel transaction level
     * @param toGlobalPotId    to global pot ID
     * @return list of transactions
     */
    List<Transaction> findByTransactionLevelAndToGlobalPotIdAndIsMasterFalse(
            TransactionLevel transactionLevel,
            String toGlobalPotId
    );

}

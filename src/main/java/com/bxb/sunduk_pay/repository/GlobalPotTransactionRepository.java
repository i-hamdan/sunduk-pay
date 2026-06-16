package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.GlobalPotTransaction;
import com.bxb.sunduk_pay.util.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalPotTransactionRepository
        extends JpaRepository<GlobalPotTransaction, String> {

    /**
     * Finds Global Pot Transactions by Global Pot ID and Transaction
     * Type with pagination.
     * @param globalPotId the ID of the Global Pot
     * @param transactionType the type of transaction
     * @param pageable the pagination information
     * @return a page of Global Pot Transactions matching the criteria
     */
    Page<GlobalPotTransaction> findByGlobalPotGlobalPotIdAndTransactionType(
            String globalPotId, TransactionType transactionType, Pageable pageable);

    /**
     * Finds Global Pot Transactions by Global Pot ID with pagination.
     * @param globalPotId the ID of the Global Pot
     * @param pageable the pagination information
     * @return a page of Global Pot Transactions for the specified Global Pot ID
     */
    Page<GlobalPotTransaction> findByGlobalPotGlobalPotId(
            String globalPotId, Pageable pageable);

}


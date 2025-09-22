package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.response.TransactionResponse;

import java.util.List;

/**
 * Mapper interface for converting Transaction entities to DTOs and Kafka events.
 */
public interface TransactionMapper {
    /**
     * Converts a single Transaction entity to a TransactionResponse DTO.
     *
     * @param transaction the Transaction entity
     * @return the corresponding TransactionResponse
     */
    TransactionResponse toTransactionResponse(Transaction transaction);

    /**
     * Converts a list of Transaction entities to a list of TransactionResponse DTOs.
     *
     * @param transactions the list of Transaction entities
     * @return the corresponding list of TransactionResponse DTOs
     */
    List<TransactionResponse> toTransactionsResponse(List<Transaction> transactions);

    /**
     * Converts a Transaction entity to a TransactionEvent for Kafka publishing.
     *
     * @param transaction the Transaction entity
     * @return the corresponding TransactionEvent
     */
    TransactionEvent toTransactionEvent(Transaction transaction);
}

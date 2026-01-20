package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.GlobalPotTransaction;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.response.GlobalPotTransactionResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implementation of GlobalPotTransactionMapper
 * for converting GlobalPotTransaction entities to DTOs.
 */
@Component
public class GlobalPotTransactionMapperImpl
        implements GlobalPotTransactionMapper {

    /**
     * Converts a list of GlobalPotTransaction entities
     * to a list of GlobalPotTransactionResponse DTOs.
     *
     * @param transactions the list of GlobalPotTransaction entities
     * @return the corresponding list of GlobalPotTransactionResponse DTOs
     */
    @Override
    public List<GlobalPotTransactionResponse>
    toGlobalPotTransactionResponseList(List<GlobalPotTransaction> transactions) {
        return transactions.stream().map(this::toGlobalPotResponse).toList();
    }

    /**
     * Converts a single GlobalPotTransaction entity
     * to a GlobalPotTransactionResponse DTO.
     *
     * @param transaction the GlobalPotTransaction entity
     * @return the corresponding GlobalPotTransactionResponse
     */
    @Override
    public GlobalPotTransactionResponse toGlobalPotResponse(
            GlobalPotTransaction transaction) {
        return GlobalPotTransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .globalPotId(transaction.getGlobalPot().getGlobalPotId())
                .globalWalletId(transaction.getGlobalWallet().getGlobalWalletId())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .transactionType(transaction.getTransactionType().toString())
                .sourceUserTransactionId(transaction.getSourceUserTransactionId())
                .createdAt(transaction.getDateTime())
        .build();
    }
}

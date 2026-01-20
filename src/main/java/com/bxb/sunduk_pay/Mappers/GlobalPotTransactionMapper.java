package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.GlobalPotTransaction;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.response.GlobalPotTransactionResponse;

import java.util.List;

/**
 * Mapper interface for converting
 * GlobalPotTransaction entities to DTOs.
 */
public interface GlobalPotTransactionMapper {

    /**
     * Converts a list of GlobalPotTransaction entities
     * to a list of GlobalPotTransactionResponse DTOs.
     *
     * @param transactions the list of GlobalPotTransaction entities
     * @return the corresponding list of GlobalPotTransactionResponse DTOs
     */
    List<GlobalPotTransactionResponse> toGlobalPotTransactionResponseList(
            List<GlobalPotTransaction> transactions);

    /**
     * Converts a single GlobalPotTransaction entity
     * to a GlobalPotTransactionResponse DTO.
     * @param transaction the GlobalPotTransaction entity
     * @return the corresponding GlobalPotTransactionResponse
     */
    GlobalPotTransactionResponse toGlobalPotResponse(
            GlobalPotTransaction transaction);
}

package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotTransactionMapper;
import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotTransaction;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotTransactionRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to fetch transactions related to the Global Pot.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class FetchGlobalPotTransactionsService implements GlobalPotOperation{

    /** Repository for accessing transaction data. */
    private final GlobalPotTransactionRepository transactionRepository;

    /** Validations specific to Global Pot operations. */
    private final GlobalPotValidations globalPotValidations;

    /** Mapper to convert Transaction entities to response DTOs. */
    private final GlobalPotTransactionMapper transactionMapper;

    /**
     * Returns the Global Pot request type handled by this implementation.
     *
     * <p>
     * This implementation specifically supports
     * {@link GlobalPotRequestType#FETCH_TRANSACTIONS}, indicating that it
     * processes requests to fetch transactions.
     * </p>
     *
     * @return the Global Pot request type as FETCH_TRANSACTIONS
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.FETCH_TRANSACTIONS;
    }

    /**
     * Fetches transactions related to the Global Pot based on the request data.
     *
     * <p>
     * This method retrieves transactions from the repository
     * and constructs a response containing the transaction details.
     * </p>
     *
     * @param request the Global Pot request containing necessary parameters
     * @return {@link GlobalPotResponse} containing fetched transaction details
     * @throws IOException if any I/O error occurs during processing
     */
    @Override
    public GlobalPotResponse perform(
            final GlobalPotRequest request) throws IOException {
        log.info("Fetching Global Pot transactions.");
        GlobalPot globalPot = globalPotValidations
                .getGlobalPot(request.getGlobalPotId());

        Sort.Direction direction;
        if ("ASC".equalsIgnoreCase(request.getSortDirection())) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(request.getPage(),
                request.getSize(),
                Sort.by(direction, request.getSortBy()));


        Page<GlobalPotTransaction> transactions;
        switch (request.getFetchTransactionType()){
            case CREDIT -> transactions = transactionRepository
                    .findByGlobalPotGlobalPotIdAndTransactionType(
                            globalPot.getGlobalPotId(),
                            TransactionType.CREDIT, pageable);
            case DEBIT -> transactions = transactionRepository
                    .findByGlobalPotGlobalPotIdAndTransactionType(
                            globalPot.getGlobalPotId(),
                            TransactionType.DEBIT, pageable);

            default -> transactions = transactionRepository
                    .findByGlobalPotGlobalPotId(globalPot.getGlobalPotId(),
                            pageable);
        }

        return GlobalPotResponse.builder()
                .message("Transactions fetched successfully")
                .transactionResponses(transactionMapper
                        .toGlobalPotTransactionResponseList(
                                transactions.getContent()))
                .build();
    }
}

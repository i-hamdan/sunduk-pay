package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.exceptions.TransactionNotFoundException;
import com.bxb.sunduk_pay.exceptions.TransactionProcessingException;
import com.bxb.sunduk_pay.exceptions.WalletNotFoundException;
import com.bxb.sunduk_pay.model.Transaction;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Service to fetch transactions of a user/sub-wallet with pagination and sorting.
 */
@Log4j2
@Service
public final class FetchTransactionsService implements WalletOperation {

    /** Validations utility for wallet and transaction checks. */
    private final Validations validations;

    /** Mapper to convert Transaction entities to response DTOs. */
    private final TransactionMapper transactionMapper;

    /**
     * Constructor for FetchTransactionsService.
     *
     * @param validations validations utility
     * @param transactionMapper transaction mapper
     */
    public FetchTransactionsService(final Validations validations,
                                    final TransactionMapper transactionMapper) {
        this.validations = validations;
        this.transactionMapper = transactionMapper;
    }

    /**
     * Returns the request type handled by this service.
     *
     * @return RequestType.FETCH_TRANSACTIONS
     */
    @Override
    public RequestType getRequestType() {
        return RequestType.FETCH_TRANSACTIONS;
    }

    /**
     * Fetches transactions for a given user and sub-wallet with pagination and sorting.
     *
     * @param mainWalletRequest request containing UUID, wallet ID, paging, and sorting info
     * @return MainWalletResponse containing the transaction history
     */
    @Override
    public MainWalletResponse perform(final MainWalletRequest mainWalletRequest) {
        log.info("Fetching transactions for user UUID: {}", mainWalletRequest.getUuid());

        try {
            // Determine sorting direction
            final Sort.Direction direction = "ASC".equalsIgnoreCase(
                    mainWalletRequest.getSortDirection())
                    ? Sort.Direction.ASC
                    : Sort.Direction.DESC;

            // Build pageable object
            final Pageable pageable = PageRequest.of(
                    mainWalletRequest.getPage(),
                    mainWalletRequest.getSize(),
                    Sort.by(direction, mainWalletRequest.getSortBy())
            );

            // Validate and fetch transactions
            final Page<Transaction> transactions = validations
                    .validateTransactionsByUuidAndSubWalletId(
                            mainWalletRequest.getUuid(),
                            mainWalletRequest.getWalletId(),
                            mainWalletRequest.getTransactionGroupId(),
                            mainWalletRequest.getPaymentMethod(),
                            mainWalletRequest.getTransactionType(),
                            pageable
                    );

            log.info("Returning {} transactions for user UUID: {} and SubWallet ID: {}",
                    transactions.getNumberOfElements(),
                    mainWalletRequest.getUuid(),
                    mainWalletRequest.getWalletId()
            );

            return MainWalletResponse.builder()
                    .transactionHistory(
                            transactionMapper.toTransactionsResponse(transactions.getContent())
                    )
                    .build();

        } catch (TransactionNotFoundException | WalletNotFoundException e) {
            log.error("Unable to find transactions. Error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Cannot retrieve transactions for UUID: {}. Error: {}",
                    mainWalletRequest.getUuid(), e.getMessage());
            throw new TransactionProcessingException(
                    "Unable to fetch transactions for UUID: "
                            + mainWalletRequest.getUuid()
                            + ". Please try again later."
            );
        }
    }
}

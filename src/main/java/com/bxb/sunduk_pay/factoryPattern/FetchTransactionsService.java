package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.exception.TransactionNotFoundException;
import com.bxb.sunduk_pay.exception.TransactionProcessingException;
import com.bxb.sunduk_pay.exception.WalletNotFoundException;
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

@Log4j2
@Service
public class FetchTransactionsService implements WalletOperation {
    private final Validations validations;
    private final TransactionMapper transactionMapper;

    public FetchTransactionsService(Validations validations, TransactionMapper transactionMapper) {
        this.validations = validations;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public RequestType getRequestType() {

        return RequestType.FETCH_TRANSACTIONS;
    }

    @Override
    public MainWalletResponse perform(MainWalletRequest mainWalletRequest) {
        log.info("Fetching transactions for user : {}", mainWalletRequest.getUuid());
        try {
            Sort.Direction direction;
            if ("ASC".equalsIgnoreCase(mainWalletRequest.getSortDirection())) {
                direction = Sort.Direction.ASC;
            } else {
                direction = Sort.Direction.DESC;
            }

            Pageable pageable = PageRequest.of(mainWalletRequest.getPage(), mainWalletRequest.getSize(), Sort.by(direction, mainWalletRequest.getSortBy()));

            Page<Transaction> transactions = validations.validateTransactionsByUuidAndSubWalletId(mainWalletRequest.getUuid(), mainWalletRequest.getWalletId(),mainWalletRequest.getTransactionGroupId(),mainWalletRequest.getTransactionType(), pageable);

            log.info("Returning {} transactions for uuid ID:{} And SubWallet ID: {}", transactions.getNumberOfElements(),mainWalletRequest.getUuid(), mainWalletRequest.getWalletId());

            return MainWalletResponse.builder().transactionHistory(transactionMapper.toTransactionsResponse(transactions.getContent())).build();

        } catch (TransactionNotFoundException | WalletNotFoundException e) {
            log.error("Some error occurred while fetching transactions. Error message : {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Cannot retrieve transactions for UUID : {}", mainWalletRequest.getUuid());
            throw new TransactionProcessingException("Unable to fetch transactions for UUID: " + mainWalletRequest.getUuid() + ". Please try again later."
            );
        }
    }
}


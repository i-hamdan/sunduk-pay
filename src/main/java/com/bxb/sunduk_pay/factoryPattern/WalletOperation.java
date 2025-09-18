package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;

/**
 * Interface representing a wallet operation in the system.
 * Each operation (CREATE, UPDATE, DELETE, TRANSFER, FETCH, etc.) implements this interface.
 */
public interface WalletOperation {

    /**
     * Returns the type of wallet operation.
     *
     * @return RequestType corresponding to this operation
     */
    RequestType getRequestType();

    /**
     * Executes the wallet operation.
     *
     * @param mainWalletRequest the request containing required details
     * @return MainWalletResponse with the result of the operation
     */
    MainWalletResponse perform(MainWalletRequest mainWalletRequest);
}

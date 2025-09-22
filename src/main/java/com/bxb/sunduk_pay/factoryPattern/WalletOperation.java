package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;

/**
 * Interface defining the contract for wallet operations.
 * Each operation must specify its request
 * type and implement the perform method.
 */
public interface WalletOperation {
    /**
     * Returns the type of request this operation handles.
     *
     * @return RequestType associated with this operation.
     */
RequestType getRequestType();

/**
     * Performs the wallet operation based on the provided request.
     *
     * @param mainWalletRequest the request containing
 *                          necessary data for the operation.
     * @return MainWalletResponse containing the result of the operation.
     */
MainWalletResponse perform(MainWalletRequest mainWalletRequest);
}

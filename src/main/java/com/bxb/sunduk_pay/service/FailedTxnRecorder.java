package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;

/**
 * Service interface for recording failed transactions.
 */
public interface FailedTxnRecorder {

    /**
     * Records a failed transaction based on the provided request object.
     *
     * @param requestObj The request object containing transaction details.
     * @return A response object indicating the result of the recording operation.
     */
    MainWalletResponse recordFailedTxn(MainWalletRequest requestObj);
}

package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;

/**
 * Service interface for recording failed wallet transactions.
 */
public interface FailedTxnRecorder {

    /**
     * Records a failed transaction for a wallet request.
     *
     * @param requestObj the wallet request that failed
     * @return a response indicating the failure status
     */
    MainWalletResponse recordFailedTxn(MainWalletRequest requestObj);
}

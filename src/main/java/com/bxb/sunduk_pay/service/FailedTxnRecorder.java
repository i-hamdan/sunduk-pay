package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;

public interface FailedTxnRecorder {
    MainWalletResponse recordFailedTxn(MainWalletRequest requestObj);
}

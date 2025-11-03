package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;

/**
 * Service interface for handling external money transfers,
 * such as UPI transfers.
 */
public interface ExternalTransferService {
    /**
     * Handles UPI transfer requests.
     *
     * @param request the request containing transfer details.
     * @return MainWalletResponse containing the result of
     * the transfer operation.
     */
    MainWalletResponse handleUPITransfer(MainWalletRequest request);
}

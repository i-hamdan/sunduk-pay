package com.bxb.sunduk_pay.factories.WalletFactory;

import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.service.AutoPayConfirmationService;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service class to handle confirmation of autopay requests.
 * Implements the WalletOperation interface to perform the
 * specific wallet operation.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class ConfirmAutopayService implements WalletOperation{

    /** Validations utility for general operations. */
    private final Validations validations;

    /** Service to handle the business logic of confirming autopay. */
    private final AutoPayConfirmationService autoPayConfirmationService;
/**
     * Returns the type of request this service handles,
 * which is CONFIRM_AUTOPAY.
     *
     * @return RequestType.CONFIRM_AUTOPAY
     */
    @Override
    public RequestType getRequestType() {
        return RequestType.AUTOPAY_CONFIRMATION;
    }

    /** Performs the confirmation of autopay based on the provided request. */
    @Override
    public MainWalletResponse perform(MainWalletRequest mainWalletRequest) {

        validations.getUserInfo(mainWalletRequest.getUuid());
        if (mainWalletRequest.getConfirm()){
            autoPayConfirmationService.confirmPayment(mainWalletRequest
                            .getConfirmationId(), mainWalletRequest.getUuid());
            return MainWalletResponse.builder().message(
                    "Autopay confirmed successfully").build();
        } else {
            autoPayConfirmationService.rejectPayment(mainWalletRequest
                    .getConfirmationId(), mainWalletRequest.getUuid());
            return MainWalletResponse.builder().message(
                    "Autopay rejected successfully").build();
        }
    }
}

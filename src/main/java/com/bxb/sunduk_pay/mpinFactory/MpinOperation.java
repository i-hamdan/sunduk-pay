package com.bxb.sunduk_pay.mpinFactory;

import com.bxb.sunduk_pay.request.MpinRequest;
import com.bxb.sunduk_pay.response.MpinResponse;
import com.bxb.sunduk_pay.util.MpinRequestType;

public interface MpinOperation {
/**
     * Returns the type of MPIN request this operation handles.
     *
     * @return MpinRequestType associated with this operation.
     */
    MpinRequestType getMpinRequestType();

    /**
     * Performs the MPIN operation based on the provided request.
     *
     * @param mpinRequest the request containing
     *                          necessary data for the operation.
     * @return MpinResponse containing the result of the operation.
     */
    MpinResponse perform(MpinRequest mpinRequest);
}

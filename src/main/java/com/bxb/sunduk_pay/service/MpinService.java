package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.MpinRequest;
import com.bxb.sunduk_pay.response.MpinResponse;

public interface MpinService {
    /**
     * Handles MPIN API requests.
     *
     * @param request the MPIN request
     * @return the MPIN response
     */
    MpinResponse mpinApi(MpinRequest request);
}

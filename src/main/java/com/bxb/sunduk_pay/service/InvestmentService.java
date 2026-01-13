package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.InvestmentRequest;
import com.bxb.sunduk_pay.response.InvestmentResponse;

public interface InvestmentService {
    /**
     * Processes an investment request and returns the corresponding response.
     *
     * @param request the investment request containing necessary details
     * @return the investment response after processing the request
     */
    InvestmentResponse investmentApi(InvestmentRequest request);
}

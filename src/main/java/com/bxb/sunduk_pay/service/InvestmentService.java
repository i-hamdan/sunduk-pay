package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.InvestmentRequest;
import com.bxb.sunduk_pay.response.InvestmentResponse;

public interface InvestmentService {

    InvestmentResponse investmentApi(InvestmentRequest request);
}

package com.bxb.sunduk_pay.factories.InvestmentFactory;

import com.bxb.sunduk_pay.request.InvestmentRequest;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import com.bxb.sunduk_pay.util.InvestmentRequestType;
import org.springframework.stereotype.Service;

@Service
public class CreateInvestmentService implements InvestmentOperation{


    @Override
    public InvestmentRequestType getInvestmentRequestType() {
        return null;
    }

    @Override
    public InvestmentResponse perform(InvestmentRequest investmentRequest) {
        return null;
    }
}

package com.bxb.sunduk_pay.factories.InvestmentFactory;

import com.bxb.sunduk_pay.request.InvestmentRequest;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import com.bxb.sunduk_pay.util.InvestmentRequestType;

/**
 * Interface defining the contract for investment operations.
 * Each operation must specify its request
 * type and implement the perform method.
 */
public interface InvestmentOperation {
    /**
     * Returns the type of investment request this operation handles.
     *
     * @return InvestmentRequesType associated with this operation.
     */
    InvestmentRequestType getInvestmentRequestType();
    /**
     * Performs the investment operation based on the provided request.
     *
     * @param investmentRequest the request containing
     *                          necessary data for the operation.
     * @return InvestmentResponse containing the result of the operation.
     */
    InvestmentResponse perform(InvestmentRequest investmentRequest);
}

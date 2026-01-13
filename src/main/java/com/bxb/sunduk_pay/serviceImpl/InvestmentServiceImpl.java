package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.factories.InvestmentFactory.InvestmentOperation;
import com.bxb.sunduk_pay.factories.InvestmentFactory.InvestmentOperationsFactory;
import com.bxb.sunduk_pay.request.InvestmentRequest;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import com.bxb.sunduk_pay.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service implementation for handling investment-related operations.
 */
@Service
@RequiredArgsConstructor
public class InvestmentServiceImpl implements InvestmentService {
    /** Factory for investment operations. */
    private final InvestmentOperationsFactory factory;
    /**
     * Processes an investment API request.
     *
     * @param request the investment request
     * @return the investment response
     */
    @Override
    public InvestmentResponse investmentApi(final InvestmentRequest request) {
        InvestmentOperation operation = factory
                .getOperation(request.getRequestType());
        return operation.perform(request);
    }
}

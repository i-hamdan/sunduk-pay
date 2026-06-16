package com.bxb.sunduk_pay.factories.InvestmentFactory;

import com.bxb.sunduk_pay.util.InvestmentRequestType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory Pattern related classes and interfaces for Investment operations.
 */
@Component
@RequiredArgsConstructor
public class InvestmentOperationsFactory {
    /**
     * Retrieves the InvestmentOperation for the given
     * InvestmentRequestType.
     * @param type the type of investment request
     * @return InvestmentOperation
     */
    private final List<InvestmentOperation> investmentOperations;

    /**
     * Initializes the investment operation map after construction.
     */
    private Map<InvestmentRequestType, InvestmentOperation>
            investmentOperationMap = new HashMap<>();

    /**
     * Gets the investment operation based on the request type.
     */
    @PostConstruct
    public void init() {
        for (InvestmentOperation operation : investmentOperations) {
            investmentOperationMap.put(operation.
                    getInvestmentRequestType(), operation);
        }
    }
    /**
     * Retrieves the InvestmentOperation for the given
     * InvestmentRequestType.
     * @param type the type of investment request
     * @return InvestmentOperation
     */
    public InvestmentOperation getOperation(
            final InvestmentRequestType type) {
        return investmentOperationMap.get(type);
    }

}

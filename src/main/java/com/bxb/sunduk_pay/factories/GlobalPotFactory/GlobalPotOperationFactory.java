package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory class responsible for selecting the correct GlobalPotOperation
 * implementation based on the RequestType provided in the GlobalPotRequest.
 */
@Component
public class GlobalPotOperationFactory {

    /** A map to hold the association between GlobalPotRequestType
     * and their corresponding GlobalPotOperation implementations.
     */
    private final Map<GlobalPotRequestType,
            GlobalPotOperation> operationMap = new HashMap<>();

    /**
     * Autowires all beans that implement the GlobalPotOperation interface.
     * This is a standard way in Spring to collect all strategy
     * pattern implementations.
     * @param operations A list of all beans implementing GlobalPotOperation.
     */
    @Autowired
    public GlobalPotOperationFactory(
            final List<GlobalPotOperation> operations) {
        // Populate the map during factory initialization
        for (GlobalPotOperation operation : operations) {
            operationMap.put(operation.getGlobalPotRequestType(), operation);
        }
    }

    /**
     * Retrieves the correct GlobalPotOperation implementation based
     * on the request type.
     * @param requestType The type of operation requested
     *                    (e.g., CREATE, UPDATE, DELETE).
     * @return The specific implementation of GlobalPotOperation.
     * @throws UnsupportedOperationException if no handler is found
     * for the given type.
     */
    public GlobalPotOperation getOperation(
            final GlobalPotRequestType requestType) {
        GlobalPotOperation operation = operationMap.get(requestType);

        if (operation == null) {
            throw new UnsupportedOperationException(
                    "No GlobalPotOperation handler found for RequestType: "
                            + requestType);
        }

        return operation;
    }

    /**
     * Executes the requested operation using the factory pattern.
     * This method is often the one called by the main service layer.
     *  @param request The incoming GlobalPotRequest
     *                 containing the RequestType.
     * @return The response from the specific operation handler.
     */
    public GlobalPotResponse performOperation(
            final GlobalPotRequest request) throws IOException {
        GlobalPotRequestType requestType = request.getGlobalPotRequestType();

        GlobalPotOperation operation = getOperation(requestType);

        // Delegate the actual work to the specific handler
        return operation.perform(request);
    }
}

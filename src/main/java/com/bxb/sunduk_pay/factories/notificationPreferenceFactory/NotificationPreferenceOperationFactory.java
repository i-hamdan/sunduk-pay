package com.bxb.sunduk_pay.factories.notificationPreferenceFactory;

import com.bxb.sunduk_pay.util.NotificationPreferenceRequestType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory class to handle operations related to notification preferences.
 * This class implements the NotificationPreferenceOperation interface
 * and provides implementations for the defined methods.
 */
@Component
@RequiredArgsConstructor
public class NotificationPreferenceOperationFactory {

    /**
     * List of operations that can be performed on notification preferences.
     */
    private final List<NotificationPreferenceOperation> operations;

    /**
     * Map to associate request types with their corresponding operations.
     */
    private final Map<NotificationPreferenceRequestType,
            NotificationPreferenceOperation> operationMap = new HashMap<>();

    /**
     * Method for initializing the operation map after the bean is constructed.
     * It populates the map with the available operations,
     * based on their request types.
     */
    @PostConstruct
    private void putValues() {
        for (NotificationPreferenceOperation operation : operations) {
            operationMap.put(operation.getPreferenceRequestType(), operation);
        }
    }

    /**
     * Method to perform the operation based on the request type.
     * It retrieves the appropriate operation from the map and executes it.
     *
     * @param requestType the type of the request indicating which operation to
     *                    perform.
     * @return NotificationPreferenceResponse containing the result of the
     * operation.
     */
    public NotificationPreferenceOperation getOperation(
            final NotificationPreferenceRequestType requestType) {
        return operationMap.get(requestType);
    }
}

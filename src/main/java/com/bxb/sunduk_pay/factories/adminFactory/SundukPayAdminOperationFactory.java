package com.bxb.sunduk_pay.factories.adminFactory;

import com.bxb.sunduk_pay.util.AdminRequestType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory component responsible for resolving
 * admin operations based on admin request type.
 * It maintains a mapping between request types
 * and their corresponding operation implementations.
 */
@Component
@RequiredArgsConstructor
public class SundukPayAdminOperationFactory {

    /**
     * List of all available admin operation implementations
     * registered in the Spring application context.
     */
    private final List<SundukPayAdminOperation> adminOperations;

    /**
     * Map holding admin request types as keys and
     * their corresponding operation implementations as values.
     */
    private Map<AdminRequestType, SundukPayAdminOperation> adminServiceMap =
            new HashMap<>();

    /**
     * Initializes the factory by populating the internal map
     * with admin request types and their matching operations.
     * This method is executed after bean construction.
     */
    @PostConstruct
    private void putValues() {
        for (SundukPayAdminOperation operation : adminOperations) {
            adminServiceMap.put(operation.getAdminRequestType(), operation);
        }
    }

    /**
     * Returns the admin operation associated with the given request type.
     *
     * @param adminRequestType the admin request type
     * @return the corresponding admin operation, or null if not found
     */
    public SundukPayAdminOperation getSundukPayAdminOperation(
            final AdminRequestType adminRequestType) {
        return adminServiceMap.get(adminRequestType);
    }
}

package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.util.RequestType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory to provide the correct WalletOperation implementation
 * based on the provided RequestType.
 */
@Component
public final class WalletOperationFactory {

    /** List of all WalletOperation implementations. */
    private final List<WalletOperation> walletOperations;

    /** Map to quickly retrieve WalletOperation by RequestType. */
    private final Map<RequestType, WalletOperation> walletServiceMap = new HashMap<>();

    /**
     * Constructor for WalletOperationFactory.
     *
     * @param walletOperations list of all WalletOperation implementations
     */
    @Autowired
    public WalletOperationFactory(final List<WalletOperation> walletOperations) {
        this.walletOperations = walletOperations;
    }

    /**
     * Initialize the factory by populating the map with all WalletOperation implementations.
     */
    @PostConstruct
    private void initialize() {
        for (final WalletOperation service : walletOperations) {
            walletServiceMap.put(service.getRequestType(), service);
        }
    }

    /**
     * Retrieve the WalletOperation implementation for the given RequestType.
     *
     * @param requestType the type of request
     * @return WalletOperation implementation, or null if not found
     */
    public WalletOperation getWalletService(final RequestType requestType) {
        return walletServiceMap.get(requestType);
    }
}

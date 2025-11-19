package com.bxb.sunduk_pay.factories.WalletFactory;

import com.bxb.sunduk_pay.util.RequestType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory to provide the correct WalletOperation implementation
 * based on the provided RequestType.
 */
@Component
@RequiredArgsConstructor
public class WalletOperationFactory {
    /** List of all WalletOperation implementations injected by Spring. */
private final List<WalletOperation> walletOperations;

/** Map to hold RequestType to WalletOperation mappings. */
   private Map<RequestType, WalletOperation> walletServiceMap = new HashMap<>();

    /**
     * Initialize the factory by populating the map with
     * all WalletOperation implementations.
     */
    @PostConstruct
private void putValues() {
for (WalletOperation service : walletOperations) {
    walletServiceMap.put(service.getRequestType(), service);
}
}

    /**
     * Retrieve the WalletOperation implementation for the given RequestType.
     * @param requestType the type of request
     * @return WalletOperation implementation, or null if not found
     */
public WalletOperation getWalletService(
        final RequestType requestType) {
return walletServiceMap.get(requestType);
}
}

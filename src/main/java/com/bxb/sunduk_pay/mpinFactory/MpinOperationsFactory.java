package com.bxb.sunduk_pay.mpinFactory;

import com.bxb.sunduk_pay.util.MpinRequestType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MpinOperationsFactory {
    /**
     * List of available MPIN operations.
     */
    private final List<MpinOperation> mpinOperations;

    /**
     * Map to hold MPIN request types and their corresponding operations.
     */
    private Map<MpinRequestType, MpinOperation> mpinServiceMap =
            new HashMap<>();

    /**
     * Initializes the MPIN service map after construction.
     */
    @PostConstruct
    private void putValues() {
        for (MpinOperation service : mpinOperations) {
            mpinServiceMap.put(service.getMpinRequestType(), service);
        }
    }

    /**
     * Retrieves the MPIN operation for the given request type.
     * @param mpinRequestType
     * @return mpinOperation implementation, or null if not found
     */
    public MpinOperation getMpinOperation(
            final MpinRequestType mpinRequestType) {
        return mpinServiceMap.get(mpinRequestType);
    }

}

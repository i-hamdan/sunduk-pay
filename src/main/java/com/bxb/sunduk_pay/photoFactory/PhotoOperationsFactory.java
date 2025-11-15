package com.bxb.sunduk_pay.photoFactory;

import com.bxb.sunduk_pay.util.PhotoRequestType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.*;
/**
 * Factory class to manage and provide PhotoOperation
 * implementations based on PhotoRequestType.
 */
@Component
@RequiredArgsConstructor
public class PhotoOperationsFactory {
    /**
     * List of all available photo operations.
     */
    private final List<PhotoOperation> photoOperations;
    /**
     * Mapping of PhotoRequestType to PhotoOperation.
     */
    private Map<PhotoRequestType, PhotoOperation>
            operationMap = new HashMap<>();

    /**
     * Initializes the operation map after construction.
     */
    @PostConstruct
    private void init() {
        for (PhotoOperation operation : photoOperations) {
            operationMap.put(operation.getPhotoRequestType(), operation);
        }
    }

    /**
     * Retrieves the PhotoOperation for the given PhotoRequestType.
     * @param type the type of photo request
     * @return PhotoOperation
     */
    public PhotoOperation getOperation(PhotoRequestType type) {
        return operationMap.get(type);
    }
}

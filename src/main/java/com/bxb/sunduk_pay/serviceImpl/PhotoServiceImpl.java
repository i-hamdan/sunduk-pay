package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.exception.InvalidPhotoException;
import com.bxb.sunduk_pay.factories.photoFactory.PhotoOperation;
import com.bxb.sunduk_pay.factories.photoFactory.PhotoOperationsFactory;
import com.bxb.sunduk_pay.request.PhotoRequest;
import com.bxb.sunduk_pay.response.PhotoResponse;
import com.bxb.sunduk_pay.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Service implementation for handling photo-related operations.
 * Async orchestration is handled at the service layer.
 */
@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private final PhotoOperationsFactory factory;
    private final Executor photoExecutor;

    @Override
    public PhotoResponse uploadPhoto(final PhotoRequest request) {

        PhotoOperation operation = factory.getOperation(request.getPhotoRequestType());
        if (operation == null) {
            throw new InvalidPhotoException("Invalid photo request type provided.");
        }

        CompletableFuture.runAsync(() -> {
            operation.perform(request);
        }, photoExecutor);

        return PhotoResponse.builder()
                .message("PFP uploaded")
                .build();
    }
}
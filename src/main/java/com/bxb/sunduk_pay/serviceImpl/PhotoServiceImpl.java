package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.exception.InvalidPhotoException;
import com.bxb.sunduk_pay.factories.photoFactory.PhotoOperation;
import com.bxb.sunduk_pay.factories.photoFactory.PhotoOperationsFactory;
import com.bxb.sunduk_pay.request.PhotoRequest;
import com.bxb.sunduk_pay.response.PhotoResponse;
import com.bxb.sunduk_pay.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
/**
 * Service implementation for handling photo-related operations.
 */
@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {
/** Factory to obtain photo operations. */
    private final PhotoOperationsFactory factory;
/**
     * Uploads a photo based on the provided request.
     *
     * @param request the photo upload request
     * @return the response after attempting to upload the photo
     */
    @Override
    public PhotoResponse uploadPhoto(final PhotoRequest request) {
        PhotoOperation operation = factory
                .getOperation(request.getPhotoRequestType());
        if (operation == null) {
             throw new InvalidPhotoException(
                     "Invalid photo request type provided."
             );
        }
        return operation.perform(request);
    }
}

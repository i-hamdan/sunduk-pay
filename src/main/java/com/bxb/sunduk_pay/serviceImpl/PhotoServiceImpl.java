package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.factories.photoFactory.PhotoOperation;
import com.bxb.sunduk_pay.factories.photoFactory.PhotoOperationsFactory;
import com.bxb.sunduk_pay.request.PhotoRequest;
import com.bxb.sunduk_pay.response.PhotoResponse;
import com.bxb.sunduk_pay.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private final PhotoOperationsFactory factory;

    @Override
    public PhotoResponse uploadPhoto(PhotoRequest request) {
        PhotoOperation operation = factory
                .getOperation(request.getPhotoRequestType());
        if (operation == null) {
            return PhotoResponse.builder()
                    .message("Invalid photo request type")
                    .build();
        }
        return operation.perform(request);
    }
}

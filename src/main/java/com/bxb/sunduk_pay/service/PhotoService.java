package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.PhotoRequest;
import com.bxb.sunduk_pay.response.PhotoResponse;
/**
 * Service interface for handling photo-related operations.
 */
public interface PhotoService {
    /**
     * Uploads a photo based on the provided request.
     *
     * @param request the photo upload request containing necessary data
     * @return the response after uploading the photo
     */
    PhotoResponse uploadPhoto(PhotoRequest request);
}

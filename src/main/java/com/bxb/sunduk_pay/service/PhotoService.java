package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.PhotoRequest;
import com.bxb.sunduk_pay.response.PhotoResponse;
/**
 * Service interface for handling photo-related operations.
 */
public interface PhotoService {
    /** Uploads a photo based on the provided request.
     * @param request the photo upload request.
     * @return the response containing photo details.
     */
    PhotoResponse uploadPhoto(PhotoRequest request);
}

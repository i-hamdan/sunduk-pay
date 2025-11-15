package com.bxb.sunduk_pay.photoFactory;

import com.bxb.sunduk_pay.request.PhotoRequest;
import com.bxb.sunduk_pay.response.PhotoResponse;
import com.bxb.sunduk_pay.util.PhotoRequestType;
/**
 * PhotoOperation defines the contract for photo operations
 * based on different photo request types.
 */
public interface PhotoOperation {
    /**
     * Returns the type of photo request this operation handles.
     * @return PhotoRequestType
     */
    PhotoRequestType getPhotoRequestType();
    /**
     * Performs the photo operation based on the provided request.
     * @param photoRequest
     * @return PhotoResponse
     */
    PhotoResponse perform(PhotoRequest photoRequest);
}

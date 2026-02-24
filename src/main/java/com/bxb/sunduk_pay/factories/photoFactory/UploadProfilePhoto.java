package com.bxb.sunduk_pay.factories.photoFactory;

import com.bxb.sunduk_pay.exception.InvalidPhotoException;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.PhotoRequest;
import com.bxb.sunduk_pay.response.PhotoResponse;
import com.bxb.sunduk_pay.util.PhotoRequestType;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * UploadProfilePhoto handles the uploading of profile photos.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class UploadProfilePhoto implements PhotoOperation {

/** * Validations instance for validating photo requests. */
    private final ProfilePhotoAsync profilePhotoAsync;

/**     * Returns the type of photo request this operation handles.
     * @return PhotoRequestType.PROFILE_PHOTO
     */
    @Override
    public PhotoRequestType getPhotoRequestType() {
        return PhotoRequestType.PROFILE_PHOTO;
    }

    /**     * Performs the profile photo upload operation.
     * @param photoRequest the request containing photo data and user information
     * @return PhotoResponse indicating the result of the operation
     */
    @Override
    public PhotoResponse perform(final PhotoRequest photoRequest) {
        log.info("Async profile photo upload started for user: "
                + photoRequest.getUuid());

        profilePhotoAsync.uploadInBackground(photoRequest);

        return PhotoResponse.builder()
                .message("Profile photo upload initiated successfully.")
                .build();
    }

}



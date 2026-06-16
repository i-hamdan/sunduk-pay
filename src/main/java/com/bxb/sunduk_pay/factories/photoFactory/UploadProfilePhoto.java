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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Handles profile photo upload operation.
 * Performs validation synchronously and saves the photo asynchronously.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class UploadProfilePhoto implements PhotoOperation {

    private final Validations validations;
    private final UserRepository userRepository;
    private final Executor photoExecutor;

    /**
     * Returns the request type handled by this operation.
     *
     * @return PhotoRequestType for profile photo
     */
    @Override
    public PhotoRequestType getPhotoRequestType() {
        return PhotoRequestType.PROFILE_PHOTO;
    }

    /**
     * Validates the request and initiates asynchronous photo upload.
     * The API response is returned immediately while the photo is saved
     * in the background.
     *
     * @param photoRequest request containing photo and user details
     * @return PhotoResponse indicating upload initiation
     */
    @Override
    public PhotoResponse perform(final PhotoRequest photoRequest) {

        validations.validatePorfilePhoto(photoRequest.getMultipartFile());

        byte[] imageBytes;
        try {
            imageBytes = photoRequest.getMultipartFile().getBytes();
        } catch (Exception e) {
            throw new InvalidPhotoException("Failed to read image file.");
        }

        User user = validations.getUserInfo(photoRequest.getUuid());
        user.setProfilePhoto(imageBytes);
        userRepository.save(user);

        return PhotoResponse.builder()
                .message("PFP uploaded")
                .build();
    }
}
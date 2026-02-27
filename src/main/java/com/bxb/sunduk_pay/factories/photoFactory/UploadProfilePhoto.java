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

        final String uuid = photoRequest.getUuid();

        CompletableFuture.runAsync(() -> {
            try {
 log.info("Async profile photo upload started for user: {}", uuid);

                User user = validations.getUserInfo(uuid);
                user.setProfilePhoto(imageBytes);
                userRepository.save(user);

log.info("Async profile photo upload completed for user: {}", uuid);

            } catch (Exception e) {
 log.error("Async profile photo upload failed for user: {}", uuid, e);
            }
        }, photoExecutor);

        return PhotoResponse.builder()
                .message("PFP uploaded")
                .build();
    }
}
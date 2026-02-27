package com.bxb.sunduk_pay.factories.photoFactory;

import com.bxb.sunduk_pay.exception.InvalidPhotoException;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.PhotoRequest;
import com.bxb.sunduk_pay.response.PhotoResponse;
import com.bxb.sunduk_pay.util.PhotoRequestType;
import com.bxb.sunduk_pay.validations.Validations;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * UploadProfilePhoto handles the uploading of profile photos.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class UploadProfilePhoto implements PhotoOperation {

    /** * Validations instance for validating photo requests. */
    private final Validations validations;

    /**     * UserRepository for accessing user data. */
    private  final UserRepository userRepository;

    /**     * Executor for handling asynchronous photo upload tasks. */
    private final ExecutorService photoExecutor =
            Executors.newFixedThreadPool(8);


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

        validations.validatePorfilePhoto(photoRequest.getMultipartFile());

        CompletableFuture.runAsync(()->{
            try {
                byte[] imageBytes= photoRequest.getMultipartFile().getBytes();

                log.info("Image bytes size  : " + imageBytes.length + "bytes");

                User user = validations.getUserInfo(photoRequest.getUuid());

                user.setProfilePhoto(imageBytes);

                userRepository.save(user);

                log.info("Async profile photo upload completed for user: "
                        + photoRequest.getUuid());

            } catch (Exception e) {
                log.error("Profile photo upload failed for user: "
                        + photoRequest.getUuid(), e);
                throw new InvalidPhotoException(e.getMessage());
            }}, photoExecutor
        );
        return PhotoResponse.builder()
                .message("Profile photo upload initiated successfully.")
                .build();
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutting down photoExecutor thread pool...");
        photoExecutor.shutdown();
    }

}



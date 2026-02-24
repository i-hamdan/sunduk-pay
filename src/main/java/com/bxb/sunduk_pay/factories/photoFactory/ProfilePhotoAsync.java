package com.bxb.sunduk_pay.factories.photoFactory;

import com.bxb.sunduk_pay.exception.InvalidPhotoException;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.PhotoRequest;
import com.bxb.sunduk_pay.validations.Validations;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ProfilePhotoAsync handles the asynchronous uploading of profile photos.
 */
@RequiredArgsConstructor
@Log4j2
@Service
public class ProfilePhotoAsync {

    /** * Validations instance for validating photo requests. */
    private final Validations validations;

    /**     * UserRepository for accessing user data. */
    private  final UserRepository userRepository;

    /**     * Executor for handling asynchronous photo upload tasks. */
    private final ExecutorService photoExecutor =
            Executors.newFixedThreadPool(2);


    /**     * Uploads the profile photo in the background.
     * @param photoRequest the request containing photo data and user information
     */

    public void uploadInBackground(final PhotoRequest photoRequest) {
        CompletableFuture.runAsync(()->{
        try {
            validations.validatePorfilePhoto(photoRequest.getMultipartFile());

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
    }
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down photoExecutor thread pool...");
        photoExecutor.shutdown();
    }
}

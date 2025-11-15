package com.bxb.sunduk_pay.photoFactory;

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
 * UploadProfilePhoto handles the uploading of profile photos
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class UploadProfilePhoto implements PhotoOperation{
    /**
     * Validations instance for user validation
     */
    private final Validations validations;
    /**
     * UserRepository for database operations
     */
    private final UserRepository userRepository;
   /**
     * Returns the type of photo request this operation handles.
     * @return PhotoRequestType
     */
    @Override
    public PhotoRequestType getPhotoRequestType() {
        return PhotoRequestType.PROFILE_PHOTO;
    }
    /**
     * Performs the photo upload operation based on the provided request.
     * @param photoRequest
     * @return PhotoResponse
     */
    @Override
    public PhotoResponse perform(PhotoRequest photoRequest) {
            log.info("Uploading profile photo for user: "
                    + photoRequest.getUuid());

            log.info(photoRequest.getMultipartFile().getContentType());

       try {

           validations.validatePorfilePhoto(photoRequest.getMultipartFile());
            byte[] imageBytes = photoRequest.getMultipartFile().getBytes();


            log.info("Photo file size: " + imageBytes.length + " bytes");

            User user = validations.getUserInfo(photoRequest.getUuid());

            user.setProfilePhoto(imageBytes);

            userRepository.save(user);

            return PhotoResponse.builder()
                    .message("Profile photo saved successfully for user: "
                            + user.getUuid())
                    .build();

        } catch (Exception e) {
            throw new InvalidPhotoException(e.getMessage());
        }
    }

    }



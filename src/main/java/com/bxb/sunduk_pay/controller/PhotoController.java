package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.request.PhotoRequest;
import com.bxb.sunduk_pay.response.PhotoResponse;
import com.bxb.sunduk_pay.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/** Controller to handle photo upload requests. */
@Log4j2
@RestController
@RequiredArgsConstructor
public class PhotoController {

    /** Service to handle photo operations. */
    private final PhotoService photoService;


    /** Endpoint to upload a photo.
     *
     * @param request the photo upload request containing
     *                necessary data.
     * @return ResponseEntity containing the photo upload response.
     */
    @PostMapping(value = "/upload-photo", consumes = {"multipart/form-data"})
    public ResponseEntity<PhotoResponse> uploadPhoto(
            @ModelAttribute final PhotoRequest request) {
     log.info("uploadPhoto endpoint called for UUID: " + request.getUuid());
        return ResponseEntity.ok(photoService.uploadPhoto(request));
    }
}

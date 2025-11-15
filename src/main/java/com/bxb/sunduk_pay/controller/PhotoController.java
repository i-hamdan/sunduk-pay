package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.PhotoRequest;
import com.bxb.sunduk_pay.response.PhotoResponse;
import com.bxb.sunduk_pay.service.PhotoService;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PhotoController {
    private final Validations validations;
    private final PhotoService photoService;


    @PostMapping(value ="/upload-photo",consumes = {"multipart/form-data"})
    public ResponseEntity<PhotoResponse> uploadPhoto(@ModelAttribute PhotoRequest request) {
     log.info("uploadPhoto endpoint called for UUID: " + request.getUuid());
        return ResponseEntity.ok(photoService.uploadPhoto(request));
    }



    //this is for testing
    @PostMapping("/get-photo")
    public ResponseEntity<byte[]> getPhoto(@RequestBody PhotoRequest request) {

        log.info(request.getUuid());

        if (request.getUuid() == null || request.getUuid().isEmpty()) {
            throw new IllegalArgumentException("UUID cannot be null or empty");
        }

        User user = validations.getUserInfo(request.getUuid());


        if (user.getProfilePhoto() == null) {
            throw new RuntimeException("User has no profile photo uploaded");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/jpeg")
                .body(user.getProfilePhoto());
    }


}

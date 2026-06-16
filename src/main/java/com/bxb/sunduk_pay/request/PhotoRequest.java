package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.PhotoRequestType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhotoRequest {
    /**
     * The unique identifier for the photo.
     */
    private String uuid;
    /**
     * The type of photo request.
     */
    private PhotoRequestType photoRequestType;
    /**
     * The multipart file representing the photo data.
     */
    private MultipartFile multipartFile;
}

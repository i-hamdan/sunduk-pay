package com.bxb.sunduk_pay.request;

import com.bxb.sunduk_pay.util.PhotoRequestType;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhotoRequest {
    private String uuid;

    private PhotoRequestType photoRequestType;
    private MultipartFile multipartFile;
}

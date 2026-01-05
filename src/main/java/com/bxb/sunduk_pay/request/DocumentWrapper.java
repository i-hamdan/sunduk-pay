package com.bxb.sunduk_pay.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class DocumentWrapper {
    private String documentHeading;
    private String documentTitle;
    private MultipartFile documentFile;
}

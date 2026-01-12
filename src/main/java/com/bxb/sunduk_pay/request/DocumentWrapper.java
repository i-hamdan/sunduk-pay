package com.bxb.sunduk_pay.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class DocumentWrapper {
    /**
     * The heading of the document.
     */
    private String documentHeading;
    /**
     * The title of the document.
     */
    private String documentTitle;
    /**
     * The file representing the document to be uploaded.
     */
    private MultipartFile documentFile;
}

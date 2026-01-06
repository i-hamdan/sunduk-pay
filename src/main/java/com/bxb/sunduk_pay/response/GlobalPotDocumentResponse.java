package com.bxb.sunduk_pay.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class GlobalPotDocumentResponse {

    private String documentHeading;

    private String documentTitle;

    private String document;

    private String documentStatus;

}

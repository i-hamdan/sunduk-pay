package com.bxb.sunduk_pay.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Response DTO for Global Pot Document details.
 */
@Getter
@Setter
public class GlobalPotDocumentResponse {

    /** Heading of the document. */
    private String documentHeading;

    /** Title of the document. */
    private String documentTitle;

    /** Content of the document. */
    private String document;

    /** Status of the document. */
    private String documentStatus;

}

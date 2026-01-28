package com.bxb.sunduk_pay.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * Wrapper class for document upload requests.
 * <p>
 * This class encapsulates the details of a document
 * including its ID, heading, title, and the file itself.
 * </p>
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentWrapper {
    /**
     * The unique identifier of the document.
     */
    private String documentId;
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

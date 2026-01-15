package com.bxb.sunduk_pay.factories.GlobalPotFactory.GlobalPotImagesOperations;


import org.springframework.stereotype.Component;

import java.util.UUID;
/**
 * Utility class for generating media paths
 * for pot images and documents.
 */
@Component
public class MediaPathUtil {

    /**
     * Generates a unique path for a pot image.
     *
     * @param potId the ID of the pot
     * @param ext   the file extension of the image
     * @return the generated image path
     */
    public  String potImage(final String potId, final String ext) {
        return "pots/" + potId + "/images/" +
                UUID.randomUUID() + "." + ext;
    }

    /**
     * Generates a unique path for a pot document.
     *
     * @param potId the ID of the pot
     * @param ext   the file extension of the document
     * @return the generated document path
     */
    public  String potDoc(final String potId, final String ext) {
        return "pots/" + potId + "/docs/"
                + UUID.randomUUID() + "." + ext;
    }
}


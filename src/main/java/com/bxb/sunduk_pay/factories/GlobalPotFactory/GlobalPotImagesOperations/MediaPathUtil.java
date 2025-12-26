package com.bxb.sunduk_pay.factories.GlobalPotFactory.GlobalPotImagesOperations;


import org.springframework.stereotype.Component;

import java.util.UUID;
/**
 * Utility class for generating media paths
 * for pot images and documents.
 */
@Component
public class MediaPathUtil {

    public  String potImage(String potId, String ext) {
        return "pots/" + potId + "/images/" +
                UUID.randomUUID() + "." + ext;
    }

    public  String potDoc(String potId, String ext) {
        return "pots/" + potId + "/docs/" +
                UUID.randomUUID() + "." + ext;
    }
}


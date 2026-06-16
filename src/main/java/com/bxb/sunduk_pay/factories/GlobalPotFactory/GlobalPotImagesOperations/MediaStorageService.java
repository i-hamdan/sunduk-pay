package com.bxb.sunduk_pay.factories.GlobalPotFactory.GlobalPotImagesOperations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class MediaStorageService {

    /**
     * Root directory for media storage, injected from application properties.
     */
    @Value("${media.root}")
    private String mediaRoot;

    /**
     * Saves a public file to the media storage.
     *
     * @param file         the file to be saved
     * @param relativePath the relative path where the file should be saved
     * @return the relative path of the saved file
     */
    public String savePublicFile(
            final MultipartFile file,
            final String relativePath) {
        try {
            Path fullPath = Paths.get(
                    mediaRoot,
                    "public",
                    relativePath
            );
// this line of code create folder if not exist
            Files.createDirectories(fullPath.getParent());

            Files.copy(
                    file.getInputStream(),
                    fullPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return relativePath;

        } catch (Exception e) {
            throw new RuntimeException("Media save failed", e);
        }
    }
}

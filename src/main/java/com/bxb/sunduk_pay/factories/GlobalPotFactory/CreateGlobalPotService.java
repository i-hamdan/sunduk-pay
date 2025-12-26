package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.GlobalPotImagesOperations.MediaPathUtil;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.GlobalPotImagesOperations.MediaStorageService;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * this service is for creating GLobalPot
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class CreateGlobalPotService implements GlobalPotOperation {

    private final GlobalPotRepository repository;
    private final GlobalPotMapper mapper;
    private final MediaStorageService mediaStorageService;
    private final MediaPathUtil mediaPathUtil;

    /**
     * Identifies this operation as CREATE_POT.
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.CREATE_POT;
    }

    /**
     * Creates a Global Pot along with optional media files.
     *
     * @param request GlobalPotRequest containing pot details and media
     * @return GlobalPotResponse containing pot ID and status message
     * @throws IOException if media storage fails
     */
    @Override
    @Transactional
    public GlobalPotResponse perform(GlobalPotRequest request) throws IOException {

        log.info("Starting Global Pot creation process");

        // Map request to entity (WITHOUT media paths)
        GlobalPot pot = mapper.toEntity(request);
        log.debug("Mapped GlobalPot entity from request");

        //  Save entity to generate GlobalPot ID
        pot = repository.save(pot);
        log.info("GlobalPot saved successfully with ID: {}",
                pot.getGlobalPotId());

        // Save media files and set paths on entity
        saveMedia(pot, request);

        //  Persist updated media paths immediately
        pot = repository.saveAndFlush(pot);
        log.info("Media paths persisted successfully for GlobalPot ID: {}",
                pot.getGlobalPotId());

        //  Build response
        return GlobalPotResponse.builder()
                .globalPotId(pot.getGlobalPotId())
                .message("Global pot created successfully")
                .build();
    }

    /**
     * Saves media files (images/documents) and updates entity with file paths.
     *
     * @param pot     Managed GlobalPot entity
     * @param request Incoming request containing media
     */
    private void saveMedia(GlobalPot pot, GlobalPotRequest request) {

        String potId = pot.getGlobalPotId();
        log.debug("Saving media for GlobalPot ID: {}", potId);

        // Primary Image
        if (request.getPrimaryImage() != null &&
                !request.getPrimaryImage().isEmpty()) {

            String path = mediaPathUtil.potImage(potId, "primary.jpg");
            mediaStorageService.savePublicFile(
                    request.getPrimaryImage(), path);

            pot.setPrimaryImage(path);
            log.info("Primary image saved at path: {}", path);
        }

        // Secondary Image
        if (request.getSecondaryImage() != null &&
                !request.getSecondaryImage().isEmpty()) {

            String path = mediaPathUtil.potImage(potId, "secondary.jpg");
            mediaStorageService.savePublicFile(
                    request.getSecondaryImage(), path);

            pot.setSecondaryImage(path);
            log.info("Secondary image saved at path: {}", path);
        }

        // KYC Document
        if (request.getKycDocument() != null &&
                !request.getKycDocument().isEmpty()) {

            String path = mediaPathUtil.potDoc(potId, "kyc.pdf");
            mediaStorageService.savePublicFile(
                    request.getKycDocument(), path);

            pot.setKycDocument(path);
            pot.setKycDocumentTitle(request.getKycDocumentTitle());

            log.info("KYC document saved at path: {}", path);
        }

        //  Institution Document
        if (request.getInstitutionDocument() != null &&
                !request.getInstitutionDocument().isEmpty()) {

            String path = mediaPathUtil.potDoc(potId, "institution.pdf");
            mediaStorageService.savePublicFile(
                    request.getInstitutionDocument(), path);

            pot.setInstitutionDocument(path);
            pot.setInstitutionDocumentTitle(
                    request.getInstitutionDocumentTitle());

            log.info("Institution document saved at path: {}", path);
        }

        //  Supporting Document
        if (request.getSupportingDocument() != null &&
                !request.getSupportingDocument().isEmpty()) {

            String path = mediaPathUtil.potDoc(potId, "supporting.pdf");
            mediaStorageService.savePublicFile(
                    request.getSupportingDocument(), path);

            pot.setSupportingDocument(path);
            pot.setSupportingDocumentTitle(
                    request.getSupportingDocumentTitle());

            log.info("Supporting document saved at path: {}", path);
        }

        //  Custom Document
        if (request.getCustomDocument() != null &&
                !request.getCustomDocument().isEmpty()) {

            String path = mediaPathUtil.potDoc(potId, "custom.pdf");
            mediaStorageService.savePublicFile(
                    request.getCustomDocument(), path);

            pot.setCustomDocument(path);
            pot.setCustomDocumentTitle(
                    request.getCustomDocumentTitle());

            log.info("Custom document saved at path: {}", path);
        }
    }
}

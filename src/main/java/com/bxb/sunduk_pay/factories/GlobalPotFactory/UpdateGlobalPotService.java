package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotDocument;
import com.bxb.sunduk_pay.repository.GlobalPotDocumentRepository;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.request.DocumentWrapper;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for updating an existing Global Pot.
 * <p>
 * This service handles the update of Global Pot details
 * and associated documents based on the provided request.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateGlobalPotService implements GlobalPotOperation {
    /**
     * Repository for Global Pot persistence operations.
     */
    private final GlobalPotRepository globalPotRepository;
    /**
     * Repository for Global Pot document persistence operations.
     */
    private final GlobalPotDocumentRepository globalPotDocumentRepository;
    /**
     * Validations for global pot operations.
     */
    private final GlobalPotValidations globalPotValidations;
    /**
     * Mapper for global pot entities and requests/responses.
     */
    private final GlobalPotMapper globalPotMapper;
    /**
     * Returns the request type supported by this service.
     * <p>
     * This method is used to map incoming requests to the
     * appropriate Global Pot operation.
     * </p>
     * @return {@link GlobalPotRequestType#Update_Global_Pot}
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.Update_Global_Pot;
    }
    /**
     * Updates an existing Global Pot along with its associated documents.
     * <p>
     * This method performs the following actions:
     * <ul>
     *     <li>Fetches the Global Pot by ID</li>
     *     <li>Updates Global Pot details from the request</li>
     *     <li>Fetches and updates associated Global Pot documents</li>
     *     <li>Saves the updated Global Pot</li>
     * </ul>
     * </p>
     * @param request the Global Pot update request
     * @return GlobalPotResponse containing update status
     * @throws IOException if any I/O error occurs
     * while processing document files
     */
    @Override
    public GlobalPotResponse perform(final GlobalPotRequest request)
            throws IOException {
        log.info("Starting Global Pot update. GlobalPot ID: {}",
                request.getGlobalPotId());
        // Fetch Global Pot by ID
        GlobalPot globalPot = globalPotValidations.getGlobalPot(
                request.getGlobalPotId());
        log.debug("Global Pot fetched successfully. ID: {}",
                globalPot.getGlobalPotId());
        // Update Global Pot details
        GlobalPot updateGlobalPot = globalPotMapper.toUpdateEntity(
                request, globalPot);
        log.info("Global Pot basic details updated. GlobalPot ID: {}",
                globalPot.getGlobalPotId());
        // Update Global Pot Documents
            List<GlobalPotDocument> documentList = new ArrayList<>();
        List<DocumentWrapper> globalPotDocumentList =
                request.getDocumentFiles();
        globalPotDocumentList.forEach(documentWrapper -> {
            String globalPotDocumentId = documentWrapper.getDocumentId();
            log.debug("Processing Global Pot document. Document ID: {}",
                    globalPotDocumentId);
            // Fetch and validate the Global Pot Document using
            // the provided document ID
            GlobalPotDocument globalPotDocument = globalPotValidations.
                    getGlobalPotDocumentId(globalPotDocumentId);
            log.debug("Validating Global Pot Document. Document ID: {}",
                    globalPotDocumentId);
            // Update document title if provided
            if (documentWrapper.getDocumentTitle() != null) {
                globalPotDocument.setDocumentTitle(documentWrapper
                        .getDocumentTitle());
            }
            // Update document heading if provided
            if (documentWrapper.getDocumentHeading() != null) {
                globalPotDocument.setDocumentHeading(documentWrapper
                        .getDocumentHeading());
            }
            // Update document file if provided
            if (documentWrapper.getDocumentFile() != null) {
                try {
                    globalPotDocument.setDocument(documentWrapper
                            .getDocumentFile().getBytes());
                    log.info("Document file updated successfully. "
                                    + "Document ID: {}",
                            globalPotDocumentId);
                } catch (IOException e) {
                    log.error("Error while reading document file. "
                                    + "Document ID: {}",
                            globalPotDocumentId, e);
                    throw new RuntimeException(e);
                }
            }
            documentList.add(globalPotDocument);
            log.debug("Global Pot document updated successfully."
                    + " Document ID: {}", globalPotDocumentId);
        });
        // Save updated Global Pot
        globalPotRepository.save(updateGlobalPot);
        log.info("Global Pot updated successfully. GlobalPot ID: {}",
                globalPot.getGlobalPotId());
        return GlobalPotResponse.builder()
                .globalPotId(globalPot.getGlobalPotId())
                .message("Global Pot Update Successfully")
                .build();
    }
}

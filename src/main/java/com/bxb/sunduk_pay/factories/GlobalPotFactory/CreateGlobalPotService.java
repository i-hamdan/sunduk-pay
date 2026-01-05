package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotDocument;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotDocumentRepository;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.DocumentWrapper;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.DocumentStatus;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.UserRoles;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * this service is for creating GLobalPot
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class CreateGlobalPotService implements GlobalPotOperation {

    private final GlobalPotRepository repository;
    private final GlobalPotMapper mapper;
    private final UserRepository userRepository;
    private final Validations validations;
    private final GlobalPotDocumentRepository globalPotDocumentRepository;


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
    public GlobalPotResponse perform(GlobalPotRequest request)
            throws IOException {

        log.info("Starting Global Pot creation process");

        GlobalPot pot = mapper.toEntity(request);
        log.debug("Mapped GlobalPot entity from request");

        List<DocumentWrapper> mediaFiles=request.getDocumentFiles();
        log.info("Saving media files for GlobalPot ID: {}",
                pot.getGlobalPotId());
        mediaFiles.forEach((wrapper)-> {
            GlobalPotDocument globalPotDocument;
            if (wrapper != null && wrapper.getDocumentFile() != null
                    && !wrapper.getDocumentFile().isEmpty()) {
                try {
                    globalPotDocument =
                            GlobalPotDocument.builder().documentHeading(wrapper.getDocumentHeading()).documentTitle(wrapper.getDocumentTitle()).document(wrapper.getDocumentFile().getBytes()).documentStatus(DocumentStatus.PENDING).globalPot(pot).build();
                    pot.addDocument(globalPotDocument);
                    log.info("Saved document: {}", wrapper.getDocumentHeading());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                log.warn("Skipping document '{}' - No file content found in " +
                        "request", wrapper.getDocumentHeading());
            }
        });

       // saving pot
        repository.save(pot);

        log.info("GlobalPot saved successfully with ID: {}",
                pot.getGlobalPotId());

        //  Build response
        return GlobalPotResponse.builder()
                .globalPotId(pot.getGlobalPotId())
                .message("Global pot created successfully")
                .build();
    }
}


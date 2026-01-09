package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotDocument;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.DocumentWrapper;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.DocumentStatus;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.UserRoles;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * this service is for creating GlobalPot.
 * It handles the creation of a new Global Pot,
 * including saving associated media files and assigning
 * administrators.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class CreateGlobalPotService implements GlobalPotOperation {

    /** Repository to manage GlobalPot data */
    private final GlobalPotRepository repository;
    /** Mapper to convert between request and entity */
    private final GlobalPotMapper mapper;
    /** Repository to manage User data */
    private final UserRepository userRepository;

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

        // Handle Media Files
        List<DocumentWrapper> mediaFiles=request.getDocumentFiles();
        log.info("Saving media files for GlobalPot ID: {}",
                pot.getGlobalPotId());
        mediaFiles.forEach((wrapper)-> {
            GlobalPotDocument globalPotDocument;
            if (wrapper != null && wrapper.getDocumentFile() != null
                    && !wrapper.getDocumentFile().isEmpty()) {
                try {
                    globalPotDocument =
                            GlobalPotDocument.builder()
                                    .documentHeading(
                                            wrapper.getDocumentHeading())
                                    .documentTitle(wrapper.getDocumentTitle())
                                    .document(wrapper.getDocumentFile()
                                            .getBytes())
                                    .documentStatus(DocumentStatus.PENDING)
                                    .globalPot(pot).build();
                    pot.addDocument(globalPotDocument);
                    log.info("Saved document: {}",
                            wrapper.getDocumentHeading());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                log.warn("Skipping document '{}' - No file content found in " +
                        "request", wrapper.getDocumentHeading());
            }
        });

        // Assign Administrators
        List<User> admins = new ArrayList<>();
        request.getAdministrators().forEach(admin ->{
            User user=
                    userRepository.findById(admin.getUuid())
                            .orElseThrow(()->new UserNotFoundException(
                                    "User not found with UUID: "
                                            +admin.getUuid()));
            if (!UserRoles.GLOBALPOT_ADMIN.equals(user.getUserRole())){
                user.setUserRole(UserRoles.GLOBALPOT_ADMIN);
                admins.add(user);
            }

        });
        userRepository.saveAll(admins);
        log.info("Assigned {} administrators to GlobalPot ID: {}",
                admins.size(), pot.getGlobalPotId());
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


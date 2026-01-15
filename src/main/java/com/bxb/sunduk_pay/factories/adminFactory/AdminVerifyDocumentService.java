package com.bxb.sunduk_pay.factories.adminFactory;

import com.bxb.sunduk_pay.exception.GlobalPotDocumentNotFoundException;
import com.bxb.sunduk_pay.model.GlobalPotDocument;
import com.bxb.sunduk_pay.repository.GlobalPotDocumentRepository;
import com.bxb.sunduk_pay.request.SundukPayAdminRequest;
import com.bxb.sunduk_pay.response.SundukPayAdminResponse;
import com.bxb.sunduk_pay.util.AdminRequestType;
import com.bxb.sunduk_pay.util.DocumentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
/**
 * Service for verifying global pot documents in the admin panel.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class AdminVerifyDocumentService implements SundukPayAdminOperation {
/**
     * Repository for accessing GlobalPotDocument data.
     */
    private final GlobalPotDocumentRepository globalPotDocumentRepository;

/**
     * This method returns the type of admin request this service handles.
     *
     * @return AdminRequestType.VERIFY_DOCUMENT
     */
    @Override
    public AdminRequestType getAdminRequestType() {
        return AdminRequestType.VERIFY_DOCUMENT;
    }
/**
     * This method verifies a global pot document based on the provided request.
     *
     * @param request SundukPayAdminRequest containing the document ID to verify
     * @return SundukPayAdminResponse with the result of the verification
     * @throws GlobalPotDocumentNotFoundException if the document is not found
     */
    @Override
    public SundukPayAdminResponse perform(final SundukPayAdminRequest request) {

        GlobalPotDocument globalPotDocument = globalPotDocumentRepository
                .findById(request.getGlobalPotDocumentId())
                .orElseThrow(() ->
                        new GlobalPotDocumentNotFoundException(
                                "Global Pot Document Not Found"));

        String message;
        if (globalPotDocument.getDocumentStatus().equals(
                DocumentStatus.PENDING)) {
            globalPotDocument.setDocumentStatus(DocumentStatus.VERIFIED);
            globalPotDocumentRepository.save(globalPotDocument);
            message = "Document Verified Successfully";
            log.info("Global Pot Document verified successfully. "
                            + "Document ID: {}",
                    globalPotDocument.getGlobalPotDocumentId());
        } else {
            message = "Document is Already Verified";

            log.info("Global Pot Document already verified. "
                            + "Document ID: {}",
                    globalPotDocument.getGlobalPotDocumentId());
        }

        return SundukPayAdminResponse.builder()
                .message(message).build();
    }
}
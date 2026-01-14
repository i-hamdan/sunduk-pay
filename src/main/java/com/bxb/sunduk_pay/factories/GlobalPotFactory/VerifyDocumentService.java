//package com.bxb.sunduk_pay.factories.GlobalPotFactory;
//
//import com.bxb.sunduk_pay.exception.GlobalPotDocumentNotFoundException;
//import com.bxb.sunduk_pay.model.GlobalPotDocument;
//import com.bxb.sunduk_pay.repository.GlobalPotDocumentRepository;
//import com.bxb.sunduk_pay.request.GlobalPotRequest;
//import com.bxb.sunduk_pay.response.GlobalPotResponse;
//import com.bxb.sunduk_pay.util.DocumentStatus;
//import com.bxb.sunduk_pay.util.GlobalPotRequestType;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//
//@Service
//@RequiredArgsConstructor
//@Log4j2
//public class VerifyDocumentService implements GlobalPotOperation{
//
//    private final GlobalPotDocumentRepository globalPotDocumentRepository;
//
//
//    /**
//     * Returns the Global Pot request type handled by this implementation.
//     *
//     * <p>
//     * This implementation specifically supports
//     * {@link GlobalPotRequestType#VERIFY_DOCUMENT}, indicating that it
//     * processes document verification requests.
//     * </p>
//     *
//     * @return the Global Pot request type as VERIFY_DOCUMENT
//     */
//    @Override
//    public GlobalPotRequestType getGlobalPotRequestType(){
//        return GlobalPotRequestType.VERIFY_DOCUMENT;
//    }
//
//    /**
//     * Verifies a Global Pot document based on the request data.
//     *
//     * <p>
//     * This method performs the following steps:
//     * <ul>
//     *     <li>Fetches the Global Pot Document using the provided document ID</li>
//     *     <li>Throws {@link GlobalPotDocumentNotFoundException} if not found</li>
//     *     <li>Verifies the document if its status is PENDING</li>
//     *     <li>Updates and persists the document status</li>
//     * </ul>
//     * </p>
//     *
//     * @param request the Global Pot request containing the document ID
//     * @return {@link GlobalPotResponse} containing verification result message
//     * @throws IOException if any I/O error occurs during processing
//     */
//    @Override
//    public GlobalPotResponse perform(GlobalPotRequest request)
//            throws IOException {
//
//        log.info("Starting Global Pot document verification." +
//                        " Document ID: {}",
//                request.getGlobalPotDocumentId());
//
//        // Fetch the Global Pot Document by ID
//        GlobalPotDocument globalPotDocument = globalPotDocumentRepository
//                .findById(request.getGlobalPotDocumentId()).orElseThrow(() ->
//                new GlobalPotDocumentNotFoundException(
//                        "Global Pot Document Not Found"));
//
//        log.debug("Global Pot Document fetched successfully. " +
//                        "Current Status: {}",
//                globalPotDocument.getDocumentStatus());
//
//        // Verify and update the document status
//        String message;
//        if(globalPotDocument.getDocumentStatus().equals(
//                DocumentStatus.PENDING)){
//            globalPotDocument.setDocumentStatus(DocumentStatus.VERIFIED);
//            globalPotDocumentRepository.save(globalPotDocument);
//            message = "Document Verified Successfully";
//            log.info("Global Pot Document verified successfully. " +
//                            "Document ID: {}",
//                    globalPotDocument.getGlobalPotDocumentId());
//        }else {
//            message = "Document is Already Verified";
//
//            log.info("Global Pot Document already verified. " +
//                            "Document ID: {}",
//                    globalPotDocument.getGlobalPotDocumentId());
//        }
//
//        // Return the response
//        return GlobalPotResponse.builder()
//                .message(message).build();
//    }
//}

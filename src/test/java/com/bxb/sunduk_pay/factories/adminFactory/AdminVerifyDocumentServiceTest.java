package com.bxb.sunduk_pay.factories.adminFactory;

import com.bxb.sunduk_pay.exception.GlobalPotDocumentNotFoundException;
import com.bxb.sunduk_pay.model.GlobalPotDocument;
import com.bxb.sunduk_pay.repository.GlobalPotDocumentRepository;
import com.bxb.sunduk_pay.request.SundukPayAdminRequest;
import com.bxb.sunduk_pay.response.SundukPayAdminResponse;
import com.bxb.sunduk_pay.util.AdminRequestType;
import com.bxb.sunduk_pay.util.DocumentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminVerifyDocumentServiceTest {

    @Mock
    private GlobalPotDocumentRepository globalPotDocumentRepository;

    @InjectMocks
    private AdminVerifyDocumentService adminVerifyDocumentService;

    @Test
    void requestTypeTest() {
        AdminRequestType type =
                adminVerifyDocumentService.getAdminRequestType();

        assertEquals(AdminRequestType.VERIFY_DOCUMENT, type);
    }

    @Test
    void verifyPendingDocumentSuccessfully() {

        String documentId = "doc-1";

        SundukPayAdminRequest request =
                SundukPayAdminRequest.builder()
                        .globalPotDocumentId(documentId)
                        .build();

        GlobalPotDocument document = new GlobalPotDocument();
        document.setGlobalPotDocumentId(documentId);
        document.setDocumentStatus(DocumentStatus.PENDING);

        when(globalPotDocumentRepository.findById(documentId))
                .thenReturn(Optional.of(document));

        SundukPayAdminResponse response =
                adminVerifyDocumentService.perform(request);

        assertNotNull(response);
        assertEquals("Document Verified Successfully",
                response.getMessage());
        assertEquals(DocumentStatus.VERIFIED,
                document.getDocumentStatus());

        verify(globalPotDocumentRepository)
                .save(document);
    }

    @Test
    void alreadyVerifiedDocumentTest() {

        String documentId = "doc-1";

        SundukPayAdminRequest request =
                SundukPayAdminRequest.builder()
                        .globalPotDocumentId(documentId)
                        .build();

        GlobalPotDocument document = new GlobalPotDocument();
        document.setGlobalPotDocumentId(documentId);
        document.setDocumentStatus(DocumentStatus.VERIFIED);

        when(globalPotDocumentRepository.findById(documentId))
                .thenReturn(Optional.of(document));

        SundukPayAdminResponse response =
                adminVerifyDocumentService.perform(request);

        assertEquals("Document is Already Verified",
                response.getMessage());

        verify(globalPotDocumentRepository, never())
                .save(any());
    }

    @Test
    void documentNotFoundTest() {

        SundukPayAdminRequest request =
                SundukPayAdminRequest.builder()
                        .globalPotDocumentId("invalid-id")
                        .build();

        when(globalPotDocumentRepository.findById("invalid-id"))
                .thenReturn(Optional.empty());

        assertThrows(
                GlobalPotDocumentNotFoundException.class,
                () -> adminVerifyDocumentService.perform(request)
        );
    }
}

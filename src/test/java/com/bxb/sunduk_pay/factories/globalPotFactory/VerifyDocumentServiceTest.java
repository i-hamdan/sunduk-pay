package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.factories.GlobalPotFactory.VerifyDocumentService;
import com.bxb.sunduk_pay.model.GlobalPotDocument;
import com.bxb.sunduk_pay.repository.GlobalPotDocumentRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.DocumentStatus;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class VerifyDocumentServiceTest {
    
    @Mock
    private GlobalPotDocumentRepository globalPotDocumentRepository;
    
    @InjectMocks
    private VerifyDocumentService verifyDocumentService;
    
    @Test
    public void requestTypeTest(){
        GlobalPotRequestType globalPotRequestType =
                verifyDocumentService.getGlobalPotRequestType();
        assertNotNull(globalPotRequestType);
        assert(globalPotRequestType.equals(GlobalPotRequestType.VERIFY_DOCUMENT));
    }
    
    @Test
    public void verifyDocumentTest() throws IOException {
        // Test implementation goes here
        
        String documentId= "5d2430c1-593e-4222-87d9-262f8aba3e6d";
        GlobalPotRequest request = new GlobalPotRequest();
        request.setGlobalPotDocumentId(documentId);
        
        GlobalPotDocument document = new GlobalPotDocument();
        document.setGlobalPotDocumentId(documentId);
        document.setDocumentStatus(DocumentStatus.PENDING);
        
        when(globalPotDocumentRepository.findById(documentId)).thenReturn(Optional.of(document));
        
        GlobalPotResponse response = verifyDocumentService.perform(request);
        
        assertNotNull(response);
        assertEquals("Document Verified Successfully", response.getMessage());
        assertEquals(DocumentStatus.VERIFIED, document.getDocumentStatus());
        
        verify(globalPotDocumentRepository).save(any(GlobalPotDocument.class));
        
    }
    
    @Test
    public void alreadyVerifiedDocumentTest() throws IOException {
        String documentId= "5d2430c1-593e-4222-87d9-262f8aba3e6d";
        
        GlobalPotRequest request = new GlobalPotRequest();
        request.setGlobalPotDocumentId(documentId);
        
        GlobalPotDocument document = new GlobalPotDocument();
        document.setGlobalPotDocumentId(documentId);
        document.setDocumentStatus(DocumentStatus.VERIFIED);
        
        when(globalPotDocumentRepository.findById(documentId)).thenReturn(Optional.of(document));
        
        GlobalPotResponse response = verifyDocumentService.perform(request);
        
        assertNotNull(response);
        assertEquals("Document is Already Verified", response.getMessage());
        assertEquals(DocumentStatus.VERIFIED, document.getDocumentStatus());
        
    }
    
}

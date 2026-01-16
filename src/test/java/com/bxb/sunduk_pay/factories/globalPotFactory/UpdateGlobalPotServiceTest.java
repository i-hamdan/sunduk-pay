package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.exception.GlobalPotNotFoundException;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.UpdateGlobalPotService;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotDocument;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.request.Creator;
import com.bxb.sunduk_pay.request.DocumentWrapper;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.*;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateGlobalPotServiceTest {
    
    @Mock
    private GlobalPotRepository globalPotRepository;
    
    @Mock
    private GlobalPotMapper globalPotMapper;
    
    @Mock
    private GlobalPotValidations globalPotValidations;
    
    @InjectMocks
    private UpdateGlobalPotService updateGlobalPotService;
    
    @Test
    public void requestTypeTest() {
        // Test implementation goes here
        GlobalPotRequestType globalPotRequestType = updateGlobalPotService
                .getGlobalPotRequestType();
        assertNotNull(globalPotRequestType);
        assertEquals(GlobalPotRequestType.Update_Global_Pot,
                globalPotRequestType);
    }
    
    @Test
    public void shouldUpdateGlobalPotSuccessfully() throws IOException {
        // Test implementation goes here
        String globalPotId = "4eded979-4ce9-4e0f-9587-65af0ac91543";
        String documentId = "dced979-4ce9-4e0f-9587-65af0ac91543";
        
        GlobalPotRequest request = new GlobalPotRequest();
        request.setGlobalPotId(globalPotId);
        request.setCaseTitle("Medical Help");
        request.setCaseCategory(CaseCategory.ALL);
        request.setPotStatus(PotStatus.ON_HOLD);
        request.setCaseRequirementType(CaseRequirementType.CRITICAL);
        request.setPotScope(PotScope.PRIVATE);
        request.setDescription("This is test description");
        request.setBeneficiaryName("John Doe");
        request.setRelationToBeneficiary("Friend");
        request.setAddress("123 Main St");
        request.setCity("Delhi");
        request.setCountry("Freedonia");
        request.setGoalDate(LocalDate.now());
        request.setGoalAmount(250000.0);
        
       Creator creator = new Creator();
       creator.setCreatedBy("User123");
       creator.setDesignation("Testing");
       request.setCreator(creator);
       
        
        DocumentWrapper documentWrapper = new DocumentWrapper();
        documentWrapper.setDocumentId(documentId);
        documentWrapper.setDocumentTitle("Aadhar");
        documentWrapper.setDocumentHeading("Identity Proof");
        
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-document.pdf",
                "application/pdf",
                "Dummy PDF content".getBytes()
        );
        documentWrapper.setDocumentFile(file);
        
        request.setDocumentFiles(List.of(documentWrapper));
        
        GlobalPot pot = new GlobalPot();
        pot.setGlobalPotId(globalPotId);
        
        GlobalPotDocument document = new GlobalPotDocument();
        document.setGlobalPotDocumentId(documentId);
        
        when(globalPotValidations.getGlobalPot(globalPotId)).thenReturn(pot);
        
        when(globalPotMapper.toUpdateEntity(request, pot)).thenReturn(pot);
        
        when(globalPotValidations.getGlobalPotDocumentId(documentId))
                .thenReturn(document);
        
        //when
        GlobalPotResponse response = updateGlobalPotService.perform(request);
        
        //then
        assertNotNull(response);
        assertEquals(globalPotId, response.getGlobalPotId());
        assertEquals("Global Pot Update Successfully",
                response.getMessage());
        
        assertEquals("Medical Help", request.getCaseTitle());
        assertEquals("Delhi", request.getCity());
        assertEquals(CaseCategory.ALL, request.getCaseCategory());
        assertEquals(PotStatus.ON_HOLD, request.getPotStatus());
        assertEquals(CaseRequirementType.CRITICAL,
                request.getCaseRequirementType());
        assertEquals(PotScope.PRIVATE,request.getPotScope());
        assertEquals("This is test description"
                ,request.getDescription());
        assertEquals("John Doe",request.getBeneficiaryName());
        assertEquals("Friend",request.getRelationToBeneficiary());
        assertEquals("123 Main St",request.getAddress());
        assertEquals("Freedonia",request.getCountry());
        
        assertEquals("Aadhar", document.getDocumentTitle());
        assertEquals("Identity Proof", document.getDocumentHeading());
        assertNotNull(document.getGlobalPotDocumentId());
        
        verify(globalPotRepository, times(1)).save(pot);
        
    }
    
    @Test
    public void shouldReturnFailureWhenExceptionOccurs() throws IOException {
        
        //given
        GlobalPotRequest request = new GlobalPotRequest();
        request.setGlobalPotId("4eded979-4ce9-4e0f-9587-65af0ac91543");
        
        //mocking
        when(globalPotValidations.getGlobalPot(request.getGlobalPotId()))
                .thenThrow(new GlobalPotNotFoundException("Id not Found"));

        //when + then
        assertThrows(GlobalPotNotFoundException.class,
                () -> updateGlobalPotService.perform(request));
        
        //then
        verify(globalPotRepository, never()).save(any());
        
    }
}

package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.UpdateGlobalPotService;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.GlobalPotDocumentRepository;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.request.DocumentWrapper;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateGlobalPotServiceTest {

    @Mock
    private Validations validations;

    @Mock
    private GlobalPotRepository globalPotRepository;

    @Mock
    private GlobalPotDocumentRepository globalPotDocumentRepository;

    @Mock
    private GlobalPotValidations globalPotValidations;

    @Mock
    private GlobalPotMapper globalPotMapper;

    @InjectMocks
    private UpdateGlobalPotService updateGlobalPotService;

    private GlobalPotRequest request;
    private User admin;
    private GlobalPot globalPot;

    @BeforeEach
    void setUp() {
        request = new GlobalPotRequest();
        request.setUuid("admin-uuid");
        request.setGlobalPotId("pot-id");
        request.setGlobalPotRequestType(GlobalPotRequestType.Update_Global_Pot);

        admin = new User();
        admin.setUuid("admin-uuid");

        globalPot = new GlobalPot();
        globalPot.setGlobalPotId("pot-id");
    }

    @Test
    void testGetGlobalPotRequestType() {
        assertEquals(GlobalPotRequestType.Update_Global_Pot,
                updateGlobalPotService.getGlobalPotRequestType());
    }

    @Test
    void testPerform_Success() throws IOException {

        when(validations.getUserInfo("admin-uuid")).thenReturn(admin);
        when(globalPotValidations.getGlobalPot("pot-id")).thenReturn(globalPot);
        when(globalPotMapper.toUpdateEntity(request, globalPot))
                .thenReturn(globalPot);

        request.setDocumentFiles(List.of());

        GlobalPotResponse response = updateGlobalPotService.perform(request);

        assertNotNull(response);
        assertEquals("pot-id", response.getGlobalPotId());
        assertEquals("Global Pot Update Successfully", response.getMessage());

        verify(globalPotRepository, times(1)).save(globalPot);
    }

    @Test
    void testPerform_AdminNotFound() {

        when(validations.getUserInfo("admin-uuid")).thenReturn(null);

        assertThrows(RuntimeException.class, () ->
                updateGlobalPotService.perform(request));
    }

    @Test
    void testPerform_UnauthorizedAdmin() {

        when(validations.getUserInfo("admin-uuid")).thenReturn(admin);
        when(globalPotValidations.getGlobalPot("pot-id"))
                .thenReturn(globalPot);

        doThrow(new RuntimeException("Unauthorized"))
                .when(globalPotValidations)
                .validateSundukPayAndGlobalPotAdmin(admin, globalPot);

        assertThrows(RuntimeException.class, () ->
                updateGlobalPotService.perform(request));
    }

    @Test
    void testPerform_WithDocumentUpdate() throws IOException {

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getBytes()).thenReturn("test".getBytes());

        DocumentWrapper wrapper = new DocumentWrapper();
        wrapper.setDocumentId("doc-id");
        wrapper.setDocumentTitle("New Title");
        wrapper.setDocumentHeading("New Heading");
        wrapper.setDocumentFile(multipartFile);

        request.setDocumentFiles(List.of(wrapper));

        GlobalPotDocument document = new GlobalPotDocument();
        document.setGlobalPotDocumentId("doc-id");

        when(validations.getUserInfo("admin-uuid")).thenReturn(admin);
        when(globalPotValidations.getGlobalPot("pot-id")).thenReturn(globalPot);
        when(globalPotMapper.toUpdateEntity(request, globalPot))
                .thenReturn(globalPot);
        when(globalPotValidations.getGlobalPotDocumentId("doc-id"))
                .thenReturn(document);

        GlobalPotResponse response = updateGlobalPotService.perform(request);

        assertEquals("New Title", document.getDocumentTitle());
        assertEquals("New Heading", document.getDocumentHeading());
        assertArrayEquals("test".getBytes(), document.getDocument());

        verify(globalPotRepository).save(globalPot);
    }

    @Test
    void testPerform_FileIOException() throws IOException {

        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getBytes()).thenThrow(new IOException());

        DocumentWrapper wrapper = new DocumentWrapper();
        wrapper.setDocumentId("doc-id");
        wrapper.setDocumentFile(multipartFile);

        request.setDocumentFiles(List.of(wrapper));

        GlobalPotDocument document = new GlobalPotDocument();

        when(validations.getUserInfo("admin-uuid")).thenReturn(admin);
        when(globalPotValidations.getGlobalPot("pot-id")).thenReturn(globalPot);
        when(globalPotMapper.toUpdateEntity(request, globalPot))
                .thenReturn(globalPot);
        when(globalPotValidations.getGlobalPotDocumentId("doc-id"))
                .thenReturn(document);

        assertThrows(RuntimeException.class, () ->
                updateGlobalPotService.perform(request));
    }
}
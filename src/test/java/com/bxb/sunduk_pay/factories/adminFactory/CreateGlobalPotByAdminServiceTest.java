package com.bxb.sunduk_pay.factories.adminFactory;

import com.bxb.sunduk_pay.Mappers.TestimonialMapper;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.Testimonial;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.request.Creator;
import com.bxb.sunduk_pay.request.DocumentWrapper;
import com.bxb.sunduk_pay.request.SundukPayAdminRequest;
import com.bxb.sunduk_pay.request.TestimonialRequest;
import com.bxb.sunduk_pay.response.SundukPayAdminResponse;
import com.bxb.sunduk_pay.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateGlobalPotByAdminServiceTest {

    @Mock private GlobalPotRepository globalPotRepository;
    @Mock private TestimonialMapper testimonialMapper;

    // Note: If you don't use these in the current logic, you can keep them mocked
    // or remove them to keep the test clean.

    @InjectMocks
    private CreateGlobalPotByAdminService createGlobalPotByAdminService;

    private SundukPayAdminRequest request;

    @BeforeEach
    void setUp() {

        User user = new User();
        user.setUuid("admin-uuid-123");

        Creator creator = new Creator();
        creator.setCreatedBy("Mohsin");
        creator.setDesignation("Dev");

        TestimonialRequest testimonialRequest = new TestimonialRequest();
        testimonialRequest.setAuthorName("Ali");
        testimonialRequest.setProfession("Teacher");
        testimonialRequest.setDetails("Great initiative!");

        MockMultipartFile file = new MockMultipartFile(
                "documentFile", "test.pdf", "application/pdf", "content".getBytes());

        DocumentWrapper doc = DocumentWrapper.builder()
                .documentHeading("ID Proof")
                .documentTitle("National ID")
                .documentFile(file)
                .build();

        request = SundukPayAdminRequest.builder()
                .caseTitle("Test Case")
                .caseCategory(CaseCategory.EMERGENCY)
                .caseRequirementType(CaseRequirementType.CRITICAL)
                .PotStatus(PotStatus.VERIFIED)
                .description("This is a test case description.")
                .address("123 Test St")
                .city("Test City")
                .country("Test Country")
                .goalAmount(15000.0)
                .goalDate(LocalDate.now().plusMonths(1))
                .creator(creator)
                .testimonials(List.of(testimonialRequest))
                .administrators(List.of(user))
                .documentFiles(List.of(doc))
                .adminNote("Admin note here.")
                .beneficiaryName("Darul Shafqat")
                .relationToBeneficiary("Friend")
                .createdByAdmin(true)
                .createdForSelf(false)
                .location("GPS coordinates")
                .build();
    }

    @Test
    @DisplayName("Should return the correct AdminRequestType")
    void getAdminRequestType_ReturnsCreatePot() {
        assertThat(createGlobalPotByAdminService.getAdminRequestType())
                .isEqualTo(AdminRequestType.CREATE_POT);
    }

    @Test
    @DisplayName("Should successfully create a Global Pot with all fields and documents")
    void perform_WithValidRequest_SavesPotAndReturnsSuccess() {
        // Arrange
        when(testimonialMapper.toEntity(any(TestimonialRequest.class))).thenReturn(new Testimonial());

        // Act
        SundukPayAdminResponse response = createGlobalPotByAdminService.perform(request);

        // Assert
        // Use ArgumentCaptor to inspect the object created inside the service
        ArgumentCaptor<GlobalPot> potCaptor = ArgumentCaptor.forClass(GlobalPot.class);
        verify(globalPotRepository, times(1)).save(potCaptor.capture());

        GlobalPot savedPot = potCaptor.getValue();

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Global pot created successfully");

        // Verify Mapping
        assertThat(savedPot.getCaseTitle()).isEqualTo("Test Case");
        assertThat(savedPot.getPotScope()).isEqualTo(PotScope.PUBLIC); // Service hardcodes this
        assertThat(savedPot.getCurrentBalance()).isEqualTo(0.0);
        assertThat(savedPot.getCreatedBy()).isEqualTo("Mohsin");

        // Verify Documents
        assertThat(savedPot.getGlobalPotDocuments()).hasSize(1);
        assertThat(savedPot.getGlobalPotDocuments().get(0).getDocumentHeading()).isEqualTo("ID Proof");
        assertThat(savedPot.getGlobalPotDocuments().get(0).getDocumentStatus()).isEqualTo(DocumentStatus.PENDING);
    }

    @Test
    @DisplayName("Negative Case: Should handle null testimonials list gracefully")
    void perform_WithNullTestimonials_InitializesEmptyList() {
        // Arrange
        request.setTestimonials(null);

        // Act
        createGlobalPotByAdminService.perform(request);

        // Assert
        ArgumentCaptor<GlobalPot> potCaptor = ArgumentCaptor.forClass(GlobalPot.class);
        verify(globalPotRepository).save(potCaptor.capture());

        assertThat(potCaptor.getValue().getTestimonials()).isNotNull();
        assertThat(potCaptor.getValue().getTestimonials()).isEmpty();
    }

    @Test
    @DisplayName("Negative Case: Should skip document processing when file content is empty")
    void perform_WithEmptyFile_SkipsDocumentButSavesPot() {
        // 1. Arrange
        // IMPORTANT: Stub the mapper because the service will call it for the testimonials in 'request'
        when(testimonialMapper.toEntity(any(TestimonialRequest.class))).thenReturn(new Testimonial());

        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "empty.txt", "text/plain", new byte[0]); // 0 bytes

        DocumentWrapper emptyDoc = DocumentWrapper.builder()
                .documentHeading("Empty Doc")
                .documentFile(emptyFile)
                .build();

        // Ensure the request has the empty doc
        request.setDocumentFiles(List.of(emptyDoc));

        // 2. Act
        SundukPayAdminResponse response = createGlobalPotByAdminService.perform(request);

        // 3. Assert
        ArgumentCaptor<GlobalPot> potCaptor = ArgumentCaptor.forClass(GlobalPot.class);
        verify(globalPotRepository).save(potCaptor.capture());

        GlobalPot savedPot = potCaptor.getValue();

        // Check that the document was skipped as expected by your logic:
        // if (!wrapper.getDocumentFile().isEmpty()) { ... } else { log.warn(...) }
        assertThat(savedPot.getGlobalPotDocuments()).isEmpty();
        assertThat(response.getMessage()).isEqualTo("Global pot created successfully");
    }
}
package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.CreateGlobalPotService;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.FollowerRepository;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.Creator;
import com.bxb.sunduk_pay.request.DocumentWrapper;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.request.TestimonialRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateGlobalPotServiceTest {

    @Mock private GlobalPotRepository repository;
    @Mock private GlobalPotMapper mapper;
    @Mock private UserRepository userRepository;
    @Mock private FollowerRepository followerRepository;

    @InjectMocks
    private CreateGlobalPotService createGlobalPotService;

    private GlobalPotRequest request;
    private GlobalPot pot;
    private User adminUser;
    private Creator creator;
    private TestimonialRequest testimonialRequest;
    private List<TestimonialRequest> testimonialsRequests;

    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setUuid("uuid-123");

        creator = new Creator();
        creator.setCreatedBy("Mohsin");
        creator.setDesignation("Dev");

        testimonialRequest  = new TestimonialRequest();
        testimonialRequest.setAuthorName("Ali");
        testimonialRequest.setProfession("Teacher");
        testimonialRequest.setDetail("Great initiative!");

        testimonialsRequests = new ArrayList<>();
        testimonialsRequests.add(testimonialRequest);

        request = new GlobalPotRequest();
        request.setCaseTitle("Emergency Relief Fund");
        request.setCaseCategory(CaseCategory.FOOD_AND_HUNGER);
        request.setCaseRequirementType(CaseRequirementType.CRITICAL);
        request.setPotScope(PotScope.PUBLIC);
        request.setPotStatus(PotStatus.PENDING_VERIFICATION);
        request.setBeneficiaryName("Orphanage Home");
        request.setRelationToBeneficiary("Volunteer");
        request.setDescription("Raising funds for emergency relief.");
        request.setCity("Bhopal");
        request.setCountry("India");
        request.setAddress("Darul Shifa Rd, Bhopal, Madhya Pradesh");
        request.setGoalAmount(5000.0);
        request.setCreatedByAdmin(false);
        request.setCreatedForSelf(false);
        request.setCreatedBy("Mohsin");
        request.setLocation("Bhopal, India");
        request.setGoalDate(LocalDate.of(2026, 01, 31));
        request.setTestimonials(testimonialsRequests);
        request.setCreator(creator);
        request.setAdministrators(List.of(adminUser));

    }

    @Test
    @DisplayName("Positive: Successfully Create Pot with Documents and Admins")
    void perform_Success() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "text/plain",
                "content".getBytes());

        DocumentWrapper doc = DocumentWrapper.builder()
                .documentHeading("ID Proof")
                .documentFile(file)
                .build();
        request.setDocumentFiles(List.of(doc));

        when(mapper.toEntity(request)).thenReturn(pot);
        when(userRepository.findById("uuid-123")).thenReturn(Optional.of(adminUser));
        // Act
        GlobalPotResponse response = createGlobalPotService.perform(request);

        // Assert
        assertThat(response.getGlobalPotId()).isEqualTo("POT-001");
        assertThat(response.getMessage()).isEqualTo("Global pot created successfully");


        verify(repository).save(pot);
    }

    @Test
    @DisplayName("Boundary: Handle Null/Empty Document Files")
    void perform_EmptyDocuments() throws IOException {
        // Arrange
        request.setDocumentFiles(new ArrayList<>()); // Empty list
        when(mapper.toEntity(request)).thenReturn(pot);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(adminUser));

        // Act
        createGlobalPotService.perform(request);

        // Assert
        assertThat(pot.getGlobalPotDocuments()).isEmpty();
        verify(repository).save(pot);
    }


    @Test
    void getGlobalPotRequestType() {
        assertThat(createGlobalPotService.getGlobalPotRequestType())
                .isEqualTo(GlobalPotRequestType.CREATE_POT);
    }
}
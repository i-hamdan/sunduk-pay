package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.DraftGlobalPotService;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.PotStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DraftGlobalPotServiceTest {

    @Mock
    private GlobalPotMapper mapper;

    @Mock
    private GlobalPotRepository globalPotRepository;

    @InjectMocks
    private DraftGlobalPotService draftGlobalPotService;

    private GlobalPotRequest potRequest;
    private GlobalPot globalPot;

    @BeforeEach
    void setUp() {
        potRequest = GlobalPotRequest.builder()
                .caseTitle("Medical Emergency")
                .goalAmount(5000.0)
                .build();

        globalPot = new GlobalPot();
        globalPot.setGlobalPotId("POT-123");
        globalPot.setCaseTitle("Medical Emergency");
    }

    @Test
    @DisplayName("Should return DRAFT_POT as the request type")
    void getGlobalPotRequestType_ReturnsCorrectType() {
        assertThat(draftGlobalPotService.getGlobalPotRequestType())
                .isEqualTo(GlobalPotRequestType.DRAFT_POT);
    }

    @Test
    @DisplayName("Should successfully draft a pot and set status to DRAFT")
    void perform_SuccessfulDraft() throws IOException {
        // Arrange
        when(mapper.toEntity(any(GlobalPotRequest.class))).thenReturn(globalPot);
        when(globalPotRepository.save(any(GlobalPot.class))).thenReturn(globalPot);

        // Act
        GlobalPotResponse response = draftGlobalPotService.perform(potRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Global Pot drafted successfully.");

        // Verify mapper was called
        verify(mapper, times(1)).toEntity(potRequest);

        // Capture the object passed to save to verify status change
        ArgumentCaptor<GlobalPot> potCaptor = ArgumentCaptor.forClass(GlobalPot.class);
        verify(globalPotRepository).save(potCaptor.capture());

        assertThat(potCaptor.getValue().getPotStatus()).isEqualTo(PotStatus.DRAFT);
    }

    @Test
    @DisplayName("Should fail when repository throws a Database Exception")
    void perform_RepositoryFails() throws IOException {
        // Arrange
        when(mapper.toEntity(any())).thenReturn(globalPot);
        when(globalPotRepository.save(any())).thenThrow(new RuntimeException("DB Connection Lost"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> draftGlobalPotService.perform(potRequest));
    }

    // --- 3. BOUNDARY / OTHER TESTS ---

    @Test
    @DisplayName("Should handle null request gracefully (if mapper allows)")
    void perform_NullRequest() throws IOException {
        // If your mapper returns null for a null request
        when(mapper.toEntity(null)).thenReturn(null);

        // Act & Assert
        // This will likely throw a NullPointerException at globalPot.setPotStatus(...)
        // Testing this helps you decide if you need a null-check in your Service
        assertThrows(NullPointerException.class, () -> draftGlobalPotService.perform(null));
    }

}
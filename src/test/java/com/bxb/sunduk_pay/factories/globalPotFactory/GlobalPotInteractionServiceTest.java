package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.factories.GlobalPotFactory.GlobalPotInteractionService;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotInteraction;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotInteractionRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.CaseCategory;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalPotInteractionServiceTest {

    @Mock
    private Validations validations;

    @Mock
    private GlobalPotValidations globalPotValidations;

    @Mock
    private GlobalPotInteractionRepository globalPotInteractionRepository;

    @InjectMocks
    private GlobalPotInteractionService service;

    private User user;
    private GlobalPot globalPot;
    private GlobalPotRequest request;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUuid("user-123");

        globalPot = new GlobalPot();
        globalPot.setGlobalPotId("pot-123");
        globalPot.setCaseCategory(CaseCategory.MEDICAL);

        request = GlobalPotRequest.builder()
                .uuid("user-123")
                .globalPotId("pot-123")
                .timeStampInSeconds(120)
                .build();
    }

    @Test
    void testGlobalPotRequestType() {
        assertEquals(
                GlobalPotRequestType.POT_INTERACTION,
                service.getGlobalPotRequestType()
        );
    }

    @Test
    void testCreateNewInteractionWhenNotExists() throws IOException {
        when(validations.getUserInfo("user-123")).thenReturn(user);
        when(globalPotValidations.getGlobalPot("pot-123"))
                .thenReturn(globalPot);
        when(globalPotInteractionRepository
                .findByUuidAndGlobalPot(user, globalPot))
                .thenReturn(Optional.empty());

        GlobalPotResponse response = service.perform(request);

        verify(globalPotInteractionRepository)
                .save(any(GlobalPotInteraction.class));
        assertEquals("Interaction recorded successfully",
                response.getMessage());
    }

    @Test
    void testUpdateExistingInteraction() throws IOException {
        GlobalPotInteraction existingInteraction =
                GlobalPotInteraction.builder()
                        .uuid(user)
                        .globalPot(globalPot)
                        .caseCategory(CaseCategory.MEDICAL)
                        .visitCount(2)
                        .totalTimeSpentInSeconds(300)
                        .lastVisitedAt(LocalDateTime.now().minusDays(1))
                        .build();

        when(validations.getUserInfo("user-123")).thenReturn(user);
        when(globalPotValidations.getGlobalPot("pot-123"))
                .thenReturn(globalPot);
        when(globalPotInteractionRepository
                .findByUuidAndGlobalPot(user, globalPot))
                .thenReturn(Optional.of(existingInteraction));

        service.perform(request);

        assertEquals(3, existingInteraction.getVisitCount());
        assertEquals(420, existingInteraction
                .getTotalTimeSpentInSeconds());
        verify(globalPotInteractionRepository).save(existingInteraction);
    }

    @Test
    void testCallValidations() throws IOException {
        when(validations.getUserInfo(anyString())).thenReturn(user);
        when(globalPotValidations.getGlobalPot(anyString()))
                .thenReturn(globalPot);
        when(globalPotInteractionRepository
                .findByUuidAndGlobalPot(any(), any()))
                .thenReturn(Optional.empty());

        service.perform(request);

        verify(validations).getUserInfo("user-123");
        verify(globalPotValidations).getGlobalPot("pot-123");
    }
}

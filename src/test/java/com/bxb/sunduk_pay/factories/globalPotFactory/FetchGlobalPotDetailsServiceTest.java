package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.FetchGlobalPotDetailsService;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotDocumentRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class FetchGlobalPotDetailsServiceTest {

    @Mock
    private GlobalPotValidations globalPotValidations;

    @Mock
    private GlobalPotMapper globalPotMapper;

    @Mock
    private Validations validations;

    @Mock
    private GlobalPotDocumentRepository globalPotDocumentRepository;

    @InjectMocks
    private FetchGlobalPotDetailsService service;

    private GlobalPotRequest request;
    private User user;
    private GlobalPot globalPot;

    @BeforeEach
    void setUp() {
        request = new GlobalPotRequest();
        request.setUuid("user-123");
        request.setGlobalPotId("pot-123");

        user = new User();
        user.setUuid("user-123");
        user.setFullName("Test User");

        globalPot = new GlobalPot();
        globalPot.setGlobalPotId("pot-123");
    }

    @Test
    void testPerform_FullAccess_ReturnsFullResponse() throws IOException {

        when(validations.getUserInfo("user-123")).thenReturn(user);
        when(globalPotValidations.getGlobalPot("pot-123"))
                .thenReturn(globalPot);
        when(globalPotValidations.hasFullAccess(user, globalPot))
                .thenReturn(true);
        when(globalPotValidations.getContributorsCount("pot-123"))
                .thenReturn(5);
        when(globalPotValidations.getFollowersCount("pot-123"))
                .thenReturn(10);

        GlobalPotResponse mappedResponse = new GlobalPotResponse();
        when(globalPotMapper.toGlobalPotResponse(globalPot))
                .thenReturn(mappedResponse);

        when(globalPotDocumentRepository
                .findByGlobalPotGlobalPotId("pot-123"))
                .thenReturn(List.of());

        GlobalPotResponse response = service.perform(request);

        assertNotNull(response);
        assertEquals(5, response.getContributorCount());
        assertEquals(10, response.getFollowerCount());

        verify(globalPotValidations)
                .hasFullAccess(user, globalPot);
    }


    @Test
    void testPerform_NoFullAccess_ReturnsBasicResponse() throws IOException {

        when(validations.getUserInfo("user-123")).thenReturn(user);
        when(globalPotValidations.getGlobalPot("pot-123"))
                .thenReturn(globalPot);
        when(globalPotValidations.hasFullAccess(user, globalPot))
                .thenReturn(false);

        GlobalPotResponse mappedResponse = new GlobalPotResponse();
        when(globalPotMapper.toGlobalPotResponse(globalPot))
                .thenReturn(mappedResponse);

        when(globalPotValidations.getFollowersCount("pot-123"))
                .thenReturn(3);
        when(globalPotValidations.getContributorsCount("pot-123"))
                .thenReturn(2);

        when(globalPotDocumentRepository
                .findByGlobalPotGlobalPotId("pot-123"))
                .thenReturn(List.of());

        GlobalPotResponse response = service.perform(request);

        assertNotNull(response);
        assertEquals(2, response.getContributorCount());
        assertEquals(3, response.getFollowerCount());

        verify(globalPotValidations)
                .hasFullAccess(user, globalPot);
    }

    @Test
    void testPerform_WhenUserNotFound_ThrowsException() {

        when(validations.getUserInfo("user-123"))
                .thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class,
                () -> service.perform(request));
    }
}
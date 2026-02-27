package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.factories.GlobalPotFactory.AcceptInvitationService;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcceptInvitationServiceTest {

    @Mock
    private Validations validations;

    @Mock
    private GlobalPotValidations globalPotValidations;

    @InjectMocks
    private AcceptInvitationService acceptInvitationService;

    private GlobalPotRequest request;
    private User user;
    private GlobalPot globalPot;

    @BeforeEach
    void setUp() {

        request = new GlobalPotRequest();
        request.setUuid("123");
        request.setGlobalPotId("pot-123");

        user = new User();
        user.setUuid(request.getUuid());
        user.setFullName("Test User");

        globalPot = new GlobalPot();
        globalPot.setGlobalPotId("pot-123");
        globalPot.setCaseTitle("Education Support Fund");
    }

    @Test
    void testGetGlobalPotRequestType() {
        assertEquals(
                GlobalPotRequestType.ACCEPT_INVITATION,
                acceptInvitationService.getGlobalPotRequestType()
        );
    }

    @Test
    void testPerform() throws IOException {

        when(validations.getUserInfo(request.getUuid()))
                .thenReturn(user);

        when(globalPotValidations.getGlobalPot(request.getGlobalPotId()))
                .thenReturn(globalPot);

        doNothing().when(globalPotValidations)
                .ensureUserIsMember(user, globalPot);

        GlobalPotResponse response =
                acceptInvitationService.perform(request);

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertTrue(response.getMessage()
                .contains("Invitation accepted successfully"));

        verify(validations, times(1))
                .getUserInfo(request.getUuid());

        verify(globalPotValidations, times(1))
                .getGlobalPot(request.getGlobalPotId());

        verify(globalPotValidations, times(1))
                .ensureUserIsMember(user, globalPot);
    }

    @Test
    void testPerformNonMember_ShouldThrowException() throws IOException {

        when(validations.getUserInfo(request.getUuid()))
                .thenReturn(user);

        when(globalPotValidations.getGlobalPot(request.getGlobalPotId()))
                .thenReturn(globalPot);

        doThrow(new RuntimeException("User not a member"))
                .when(globalPotValidations)
                .ensureUserIsMember(user, globalPot);

        assertThrows(RuntimeException.class, () ->
                acceptInvitationService.perform(request)
        );
    }
}
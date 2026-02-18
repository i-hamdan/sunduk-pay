package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.factories.GlobalPotFactory.LeaveGlobalPotService;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotMembers;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotMembersRepository;
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
class LeaveGlobalPotServiceTest {

    @Mock
    private Validations validations;

    @Mock
    private GlobalPotValidations globalPotValidations;

    @Mock
    private GlobalPotMembersRepository globalPotMembersRepository;

    @InjectMocks
    private LeaveGlobalPotService leaveGlobalPotService;

    private GlobalPotRequest request;
    private User user;
    private GlobalPot globalPot;
    private GlobalPotMembers member;

    @BeforeEach
    void setUp() {
        request = new GlobalPotRequest();
        request.setUuid("user-123");
        request.setGlobalPotId("pot-123");

        user = new User();
        user.setUuid("user-123");

        globalPot = new GlobalPot();
        globalPot.setGlobalPotId("pot-123");

        member = new GlobalPotMembers();
    }


    @Test
    void testGetGlobalPotRequestType() {
        GlobalPotRequestType type = leaveGlobalPotService
                .getGlobalPotRequestType();
        assertEquals(GlobalPotRequestType.LEAVE_GLOBAL_POT, type);
    }

    @Test
    void testPerform_Success() throws IOException {


        when(validations.getUserInfo("user-123")).thenReturn(user);
        when(globalPotValidations.getGlobalPot("pot-123"))
                .thenReturn(globalPot);
        when(globalPotValidations
                .getMemberByUuidAndGlobalPotId(user, globalPot))
                .thenReturn(member);

        GlobalPotResponse response =
                leaveGlobalPotService.perform(request);

        assertNotNull(response);
        assertEquals(
                "User with ID: user-123 has left Global " +
                        "Pot with ID: pot-123", response.getMessage()
        );


        verify(validations, times(1))
                .getUserInfo("user-123");

        verify(globalPotValidations, times(1))
                .getGlobalPot("pot-123");

        verify(globalPotValidations, times(1))
                .getMemberByUuidAndGlobalPotId(user, globalPot);

        verify(globalPotMembersRepository, times(1))
                .delete(member);
    }
}

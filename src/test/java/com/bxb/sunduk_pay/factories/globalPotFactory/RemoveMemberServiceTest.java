package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.RemoveMemberService;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotMembers;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotMembersRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveMemberServiceTest {

    @Mock
    private Validations validations;

    @Mock
    private GlobalPotValidations globalPotValidations;

    @Mock
    private GlobalPotMembersRepository globalPotMembersRepository;

    @InjectMocks
    private RemoveMemberService removeMemberService;

    @Test
    void testRemoveMemberSuccessfully() throws IOException {

        GlobalPotRequest request = new GlobalPotRequest();
        request.setAdminUuid("admin-123");
        request.setGlobalPotId("pot-123");
        request.setTargetUserToRemove("user-123");

        User admin = new User();
        admin.setUuid("admin-123");

        User targetUser = new User();
        targetUser.setUuid("user-123");

        GlobalPot globalPot = new GlobalPot();
        globalPot.setGlobalPotId("pot-123");

        GlobalPotMembers member = new GlobalPotMembers();

        when(validations.getUserInfo("admin-123")).thenReturn(admin);
        when(validations.getUserInfo("user-123")).thenReturn(targetUser);
        when(globalPotValidations.getGlobalPot("pot-123")).thenReturn(globalPot);
        when(globalPotMembersRepository
                .findByUserAndGlobalPot(targetUser, globalPot))
                .thenReturn(Optional.of(member));

        GlobalPotResponse response = removeMemberService.perform(request);

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(
                "User removed from Global Pot successfully",
                response.getMessage()
        );

        verify(globalPotValidations).validateAdmin(admin);
        verify(globalPotMembersRepository).delete(member);
    }

    @Test
    void perform_shouldThrowResourceNotFoundException_whenMembershipNotFound()
            throws IOException {

        GlobalPotRequest request = new GlobalPotRequest();
        request.setAdminUuid("admin-123");
        request.setGlobalPotId("pot-123");
        request.setTargetUserToRemove("user-123");

        User admin = new User();
        admin.setUuid("admin-123");

        User targetUser = new User();
        targetUser.setUuid("user-123");

        GlobalPot globalPot = new GlobalPot();
        globalPot.setGlobalPotId("pot-123");

        when(validations.getUserInfo("admin-123")).thenReturn(admin);
        when(validations.getUserInfo("user-123")).thenReturn(targetUser);
        when(globalPotValidations.getGlobalPot("pot-123")).thenReturn(globalPot);
        when(globalPotMembersRepository
                .findByUserAndGlobalPot(targetUser, globalPot))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> removeMemberService.perform(request)
        );

        assertEquals("Membership record not found", exception.getMessage());

        verify(globalPotMembersRepository, never()).delete(any());
    }
}

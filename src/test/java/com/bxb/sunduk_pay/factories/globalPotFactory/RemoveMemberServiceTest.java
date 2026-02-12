package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.RemoveMemberService;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotMembers;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotMembersRepository;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
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

    @Mock
    private GlobalPotRepository globalPotRepository;

    @InjectMocks
    private RemoveMemberService removeMemberService;

    private GlobalPotRequest request;
    private User admin;
    private GlobalPot globalPot;
    private GlobalPotMembers member;

    @BeforeEach
    void setUp() {
        request = new GlobalPotRequest();
        request.setAdminUuid("admin-uuid");
        request.setGlobalPotId("pot-1");
        request.setTargetUsersToRemove(List.of("user-uuid"));

        admin = new User();
        admin.setUuid("admin-uuid");

        globalPot = new GlobalPot();
        globalPot.setGlobalPotId("pot-1");

        member = new GlobalPotMembers();
    }

    @Test
    void testRemoveMemberSuccessfully() throws IOException {

        when(validations.getUserInfo("admin-uuid"))
                .thenReturn(admin);

        when(globalPotValidations.getGlobalPot("pot-1"))
                .thenReturn(globalPot);

        doNothing().when(globalPotValidations)
                .validateAdmin(admin, globalPot);

        when(globalPotMembersRepository
                .findByUserUuidAndGlobalPotGlobalPotId("user-uuid",
                        "pot-1"))
                .thenReturn(Optional.of(member));

        GlobalPotResponse response =
                removeMemberService.perform(request);

        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals("User removed from Global Pot successfully",
                response.getMessage());

        verify(globalPotMembersRepository, times(1))
                .deleteAll(anyList());
    }

    @Test
    void testThrowExceptionWhenMembershipNotFound() throws IOException {

        when(validations.getUserInfo("admin-uuid"))
                .thenReturn(admin);

        when(globalPotValidations.getGlobalPot("pot-1"))
                .thenReturn(globalPot);

        doNothing().when(globalPotValidations)
                .validateAdmin(admin, globalPot);

        when(globalPotMembersRepository
                .findByUserUuidAndGlobalPotGlobalPotId
                        ("user-uuid", "pot-1"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> removeMemberService.perform(request));

        verify(globalPotMembersRepository, never())
                .deleteAll(anyList());
    }

    @Test
    void testThrowExceptionWhenAdminNotFound() throws IOException {

        when(validations.getUserInfo("admin-uuid"))
                .thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class,
                () -> removeMemberService.perform(request));

        verifyNoInteractions(globalPotMembersRepository);
    }

    @Test
    void testThrowExceptionWhenGlobalPotNotFound() throws IOException {

        when(validations.getUserInfo("admin-uuid"))
                .thenReturn(admin);

        when(globalPotValidations.getGlobalPot("pot-1"))
                .thenThrow(new RuntimeException("Pot not found"));

        assertThrows(RuntimeException.class,
                () -> removeMemberService.perform(request));

        verifyNoInteractions(globalPotMembersRepository);
    }
}

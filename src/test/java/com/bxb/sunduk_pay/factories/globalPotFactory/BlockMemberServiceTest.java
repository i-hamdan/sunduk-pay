package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.exception.UserIsBlockedException;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.BlockMemberService;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotMembers;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotMembersRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockMemberServiceTest {

    @Mock
    private GlobalPotValidations globalPotValidations;

    @Mock
    private Validations validations;

    @Mock
    private GlobalPotMembersRepository membersRepository;

    @InjectMocks
    private BlockMemberService service;

    @Test
    void testGetGlobalPotRequestType_ShouldReturnBlockUser() {
        GlobalPotRequestType type = service.getGlobalPotRequestType();
        assertEquals(GlobalPotRequestType.BLOCK_USER, type);
    }

    @Test
    void testPerform_BlockUserSuccess() {
        // Arrange
        GlobalPotRequest request = GlobalPotRequest.builder()
                .adminUuid("admin-uuid")
                .globalPotId("pot-uuid")
                .targetUserUuid("target-uuid")
                .build();

        User admin = new User();
        admin.setUuid("admin-uuid");

        User targetUser = new User();
        targetUser.setUuid("target-uuid");

        GlobalPot globalPot = new GlobalPot();
        globalPot.setGlobalPotId("pot-uuid");

        GlobalPotMembers member = new GlobalPotMembers();
        member.setUser(targetUser);
        member.setIsBlocked(false);

        when(validations.getUserInfo("admin-uuid")).thenReturn(admin);
        when(globalPotValidations.getGlobalPot("pot-uuid")).thenReturn(globalPot);
        doNothing().when(globalPotValidations).validateSundukPayAndGlobalPotAdmin(admin, globalPot);

        when(validations.getUserInfo("target-uuid")).thenReturn(targetUser);
        when(globalPotValidations.getMemberByUuidAndGlobalPotId(targetUser, globalPot))
                .thenReturn(member);

        when(membersRepository.save(member)).thenReturn(member);

        // Act
        GlobalPotResponse response = service.perform(request);

        // Assert
        assertNotNull(response);
        assertEquals("User has been blocked successfully", response.getMessage());
        assertTrue(member.getIsBlocked());

        verify(membersRepository, times(1)).save(member);
    }

    @Test
    void testPerform_WhenUserAlreadyBlocked_ShouldThrowException() {
        // Arrange
        GlobalPotRequest request = GlobalPotRequest.builder()
                .adminUuid("admin-uuid")
                .globalPotId("pot-uuid")
                .targetUserUuid("target-uuid")
                .build();

        User admin = new User();
        admin.setUuid("admin-uuid");

        User targetUser = new User();
        targetUser.setUuid("target-uuid");

        GlobalPot globalPot = new GlobalPot();
        globalPot.setGlobalPotId("pot-uuid");

        GlobalPotMembers member = new GlobalPotMembers();
        member.setUser(targetUser);
        member.setIsBlocked(true); // already blocked

        when(validations.getUserInfo("admin-uuid")).thenReturn(admin);
        when(globalPotValidations.getGlobalPot("pot-uuid")).thenReturn(globalPot);
        doNothing().when(globalPotValidations).validateSundukPayAndGlobalPotAdmin(admin, globalPot);

        when(validations.getUserInfo("target-uuid")).thenReturn(targetUser);
        when(globalPotValidations.getMemberByUuidAndGlobalPotId(targetUser, globalPot))
                .thenReturn(member);

        // Act + Assert
        UserIsBlockedException ex = assertThrows(
                UserIsBlockedException.class,
                () -> service.perform(request)
        );

        assertTrue(ex.getMessage().contains("already blocked"));

        verify(membersRepository, never()).save(any());
    }

    @Test
    void testPerform_WhenAdminValidationFails_ShouldThrowException() {
        // Arrange
        GlobalPotRequest request = GlobalPotRequest.builder()
                .adminUuid("admin-uuid")
                .globalPotId("pot-uuid")
                .targetUserUuid("target-uuid")
                .build();

        User admin = new User();
        admin.setUuid("admin-uuid");

        GlobalPot globalPot = new GlobalPot();
        globalPot.setGlobalPotId("pot-uuid");

        when(validations.getUserInfo("admin-uuid")).thenReturn(admin);
        when(globalPotValidations.getGlobalPot("pot-uuid")).thenReturn(globalPot);

        doThrow(new RuntimeException("Only admin can perform this action"))
                .when(globalPotValidations).validateSundukPayAndGlobalPotAdmin(admin, globalPot);

        // Act + Assert
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.perform(request)
        );

        assertTrue(ex.getMessage().contains("admin"));

        verify(membersRepository, never()).save(any());
    }
}

package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.exception.UserNotFoundException;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.BlockMemberService;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotBlockedUser;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotBlockedUserRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.MpinRequestType;
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
    private GlobalPotBlockedUserRepository blockedUserRepository;

    @Mock
    private Validations validations;

    @InjectMocks
    private BlockMemberService service;




    @Test
    void testGetMpinRequestType_ShouldReturnOTP() {
        GlobalPotRequestType type = service.getGlobalPotRequestType();

        assertEquals(GlobalPotRequestType.BLOCK_USER, type,
                "this is service Request Type should be Block_User");
    }



    @Test
    void testBlockUserSuccess() {

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

        when(validations.getUserInfo("admin-uuid")).thenReturn(admin);
        when(validations.getUserInfo("target-uuid")).thenReturn(targetUser);
        when(globalPotValidations.getGlobalPot("pot-uuid")).thenReturn(globalPot);

        // validation methods do nothing (pass)
        doNothing().when(globalPotValidations).validateAdmin(admin);
        doNothing().when(globalPotValidations)
                .validateNotAlreadyBlocked("pot-uuid", "target-uuid");

        GlobalPotResponse response = service.perform(request);

        assertNotNull(response);
        assertEquals("User has been blocked successfully", response.getMessage());

        verify(blockedUserRepository, times(1))
                .save(any(GlobalPotBlockedUser.class));
    }


    @Test
    void testBlockUser_AdminNotAuthorized() {

        GlobalPotRequest request = GlobalPotRequest.builder()
                .adminUuid("admin-uuid")
                .globalPotId("pot-uuid")
                .targetUserUuid("target-uuid")
                .build();

        User admin = new User();
        admin.setUuid("admin-uuid");

        when(validations.getUserInfo("admin-uuid")).thenReturn(admin);

        doThrow(new UserNotFoundException("Only admin can perform this action"))
                .when(globalPotValidations).validateAdmin(admin);

        UserNotFoundException ex = assertThrows(
                UserNotFoundException.class,
                () -> service.perform(request)
        );

        assertTrue(ex.getMessage().contains("admin"));

        verify(blockedUserRepository, never()).save(any());
    }



    @Test
    void testBlockUser_GlobalPotNotFound() {

        GlobalPotRequest request = GlobalPotRequest.builder()
                .adminUuid("admin-uuid")
                .globalPotId("invalid-pot")
                .targetUserUuid("target-uuid")
                .build();

        User admin = new User();
        admin.setUuid("admin-uuid");

        when(validations.getUserInfo("admin-uuid")).thenReturn(admin);

        doNothing().when(globalPotValidations).validateAdmin(admin);

        when(globalPotValidations.getGlobalPot("invalid-pot"))
                .thenThrow(new ResourceNotFoundException("Global Pot not found"));

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> service.perform(request)
        );

        assertTrue(ex.getMessage().contains("Global Pot"));

        verify(blockedUserRepository, never()).save(any());
    }


    @Test
    void testBlockUser_AlreadyBlocked() {

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

        doNothing().when(globalPotValidations).validateAdmin(admin);

        doThrow(new ResourceNotFoundException("User already blocked"))
                .when(globalPotValidations)
                .validateNotAlreadyBlocked("pot-uuid", "target-uuid");

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> service.perform(request)
        );

        assertTrue(ex.getMessage().contains("already"));

        verify(blockedUserRepository, never()).save(any());
    }



    @Test
    void testBlockUser_TargetUserNotFound() {

        GlobalPotRequest request = GlobalPotRequest.builder()
                .adminUuid("admin-uuid")
                .globalPotId("pot-uuid")
                .targetUserUuid("missing-user")
                .build();

        User admin = new User();
        admin.setUuid("admin-uuid");

        GlobalPot globalPot = new GlobalPot();
        globalPot.setGlobalPotId("pot-uuid");

        when(validations.getUserInfo("admin-uuid")).thenReturn(admin);
        when(globalPotValidations.getGlobalPot("pot-uuid")).thenReturn(globalPot);

        doNothing().when(globalPotValidations).validateAdmin(admin);
        doNothing().when(globalPotValidations)
                .validateNotAlreadyBlocked("pot-uuid", "missing-user");

        when(validations.getUserInfo("missing-user"))
                .thenThrow(new ResourceNotFoundException("User not found"));

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> service.perform(request)
        );

        assertTrue(ex.getMessage().contains("User"));

        verify(blockedUserRepository, never()).save(any());
    }
}

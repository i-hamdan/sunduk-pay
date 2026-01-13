package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.factories.GlobalPotFactory.AddMemberService;
import com.bxb.sunduk_pay.model.GlobalPot;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddMemberServiceTest {

    @Mock
    private Validations validations;

    @Mock
    private GlobalPotValidations globalPotValidations;

    @Mock
    private GlobalPotMembersRepository globalPotMembersRepository;

    @InjectMocks
    private AddMemberService addMemberService;

    @Test
    void testAddMemberSuccessfully() throws Exception {

        User admin = User.builder().uuid("admin-1").build();
        User targetUser = User.builder().uuid("user-1").build();

        GlobalPot pot = GlobalPot.builder()
                .globalPotId("pot-1")
                .build();

        GlobalPotRequest request = GlobalPotRequest.builder()
                .adminUuid("admin-1")
                .globalPotId("pot-1")
                .targetUserToAdd("user-1")
                .build();

        when(validations.getUserInfo("admin-1")).thenReturn(admin);
        when(validations.getUserInfo("user-1")).thenReturn(targetUser);
        when(globalPotValidations.getGlobalPot("pot-1")).thenReturn(pot);

        doNothing().when(globalPotValidations).validateAdmin(admin);
        doNothing().when(globalPotValidations)
                .ensureUserIsMemberByAdmin(targetUser, pot);

        GlobalPotResponse response = addMemberService.perform(request);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals("User added to Global Pot successfully",
                response.getMessage());

        verify(validations).getUserInfo("admin-1");
        verify(validations).getUserInfo("user-1");
        verify(globalPotValidations).validateAdmin(admin);
        verify(globalPotValidations).ensureUserIsMemberByAdmin(targetUser, pot);
    }

    @Test
    void testUserIsNotAdmin() {

        User user = User.builder().uuid("user-1").build();

        GlobalPotRequest request = GlobalPotRequest.builder()
                .adminUuid("user-1")
                .build();

        when(validations.getUserInfo("user-1")).thenReturn(user);
        doThrow(new RuntimeException("Not admin"))
                .when(globalPotValidations).validateAdmin(user);

        assertThrows(
                RuntimeException.class,
                () -> addMemberService.perform(request)
        );

        verify(globalPotValidations).validateAdmin(user);
        verifyNoMoreInteractions(globalPotValidations);
    }

    @Test
    void testGlobalPotNotFound() {

        User admin = User.builder().uuid("admin-1").build();

        GlobalPotRequest request = GlobalPotRequest.builder()
                .adminUuid("admin-1")
                .globalPotId("invalid-pot")
                .build();

        when(validations.getUserInfo("admin-1")).thenReturn(admin);
        doNothing().when(globalPotValidations).validateAdmin(admin);

        doThrow(new RuntimeException("Global pot not found"))
                .when(globalPotValidations)
                .getGlobalPot("invalid-pot");

        assertThrows(
                RuntimeException.class,
                () -> addMemberService.perform(request)
        );

        verify(globalPotValidations).getGlobalPot("invalid-pot");
    }

    @Test
    void testUserAlreadyMemberException() {

        User admin = User.builder().uuid("admin-1").build();
        User targetUser = User.builder().uuid("user-1").build();

        GlobalPot pot = GlobalPot.builder().build();

        GlobalPotRequest request = GlobalPotRequest.builder()
                .adminUuid("admin-1")
                .globalPotId("pot-1")
                .targetUserToAdd("user-1")
                .build();

        when(validations.getUserInfo("admin-1")).thenReturn(admin);
        when(validations.getUserInfo("user-1")).thenReturn(targetUser);
        when(globalPotValidations.getGlobalPot("pot-1")).thenReturn(pot);

        doNothing().when(globalPotValidations).validateAdmin(admin);
        doThrow(new RuntimeException("User already member"))
                .when(globalPotValidations)
                .ensureUserIsMemberByAdmin(targetUser, pot);

        assertThrows(
                RuntimeException.class,
                () -> addMemberService.perform(request)
        );

        verify(globalPotValidations)
                .ensureUserIsMemberByAdmin(targetUser, pot);
    }
}

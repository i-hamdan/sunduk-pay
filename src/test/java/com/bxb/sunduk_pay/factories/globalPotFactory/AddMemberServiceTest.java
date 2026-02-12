package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.factories.GlobalPotFactory.AddMemberService;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddMemberServiceTest {

    @Mock
    private Validations validations;

    @Mock
    private GlobalPotValidations globalPotValidations;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddMemberService addMemberService;

    @Test
    void testAddMembersSuccessfully() throws Exception {

        User admin = User.builder().uuid("admin-1").build();
        User user1 = User.builder().uuid("user-1").build();
        User user2 = User.builder().uuid("user-2").build();

        GlobalPot pot = GlobalPot.builder()
                .globalPotId("pot-1")
                .build();

        GlobalPotRequest request = GlobalPotRequest.builder()
                .adminUuid("admin-1")
                .globalPotId("pot-1")
                .targetUsersToAdd(List.of("user-1", "user-2"))
                .build();

        when(validations.getUserInfo("admin-1")).thenReturn(admin);
        when(globalPotValidations.getGlobalPot("pot-1")).thenReturn(pot);
        when(userRepository.findAllById(List.of("user-1", "user-2")))
                .thenReturn(List.of(user1, user2));

        doNothing().when(globalPotValidations).validateAdmin(admin, pot);
        doNothing().when(globalPotValidations).ensureUserIsMember(any(), eq(pot));

        GlobalPotResponse response = addMemberService.perform(request);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals("User added to Global Pot successfully",
                response.getMessage());

        verify(validations).getUserInfo("admin-1");
        verify(globalPotValidations).validateAdmin(admin, pot);
        verify(globalPotValidations, times(2))
                .ensureUserIsMember(any(), eq(pot));
    }

    @Test
    void testUserIsNotAdmin() {

        User user = User.builder().uuid("user-1").build();
        GlobalPot pot = GlobalPot.builder()
                .globalPotId("pot-1")
                .build();

        GlobalPotRequest request = GlobalPotRequest.builder()
                .adminUuid("user-1")
                .globalPotId("pot-1")
                .targetUsersToAdd(List.of("user-2"))
                .build();

        when(validations.getUserInfo("user-1")).thenReturn(user);
        when(globalPotValidations.getGlobalPot("pot-1")).thenReturn(pot);

        doThrow(new RuntimeException("Not admin"))
                .when(globalPotValidations).validateAdmin(user, pot);

        assertThrows(
                RuntimeException.class,
                () -> addMemberService.perform(request)
        );

        verify(globalPotValidations).validateAdmin(user, pot);
        verify(userRepository, never()).findAllById(any());
    }

    @Test
    void testGlobalPotNotFound() {

        User admin = User.builder().uuid("admin-1").build();

        GlobalPotRequest request = GlobalPotRequest.builder()
                .adminUuid("admin-1")
                .globalPotId("invalid-pot")
                .targetUsersToAdd(List.of("user-1"))
                .build();

        when(validations.getUserInfo("admin-1")).thenReturn(admin);

        doThrow(new RuntimeException("Global pot not found"))
                .when(globalPotValidations)
                .getGlobalPot("invalid-pot");

        assertThrows(
                RuntimeException.class,
                () -> addMemberService.perform(request)
        );

        verify(globalPotValidations).getGlobalPot("invalid-pot");
        verify(userRepository, never()).findAllById(any());
    }

    @Test
    void testUserAlreadyMemberException() {

        User admin = User.builder().uuid("admin-1").build();
        User user1 = User.builder().uuid("user-1").build();

        GlobalPot pot = GlobalPot.builder()
                .globalPotId("pot-1")
                .build();

        GlobalPotRequest request = GlobalPotRequest.builder()
                .adminUuid("admin-1")
                .globalPotId("pot-1")
                .targetUsersToAdd(List.of("user-1"))
                .build();

        when(validations.getUserInfo("admin-1")).thenReturn(admin);
        when(globalPotValidations.getGlobalPot("pot-1")).thenReturn(pot);
        when(userRepository.findAllById(List.of("user-1")))
                .thenReturn(List.of(user1));

        doNothing().when(globalPotValidations).validateAdmin(admin, pot);
        doThrow(new RuntimeException("User already member"))
                .when(globalPotValidations)
                .ensureUserIsMember(user1, pot);

        assertThrows(
                RuntimeException.class,
                () -> addMemberService.perform(request)
        );

        verify(globalPotValidations)
                .ensureUserIsMember(user1, pot);
    }
}

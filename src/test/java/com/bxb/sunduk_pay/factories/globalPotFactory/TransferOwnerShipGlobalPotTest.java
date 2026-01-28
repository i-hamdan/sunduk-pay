package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.factories.GlobalPotFactory.TransferOwnerShipGlobalPot;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotMembers;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotMembersRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.UserRoles;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferOwnerShipGlobalPotTest {

    @Mock
    private GlobalPotValidations globalPotValidations;

    @Mock
    private Validations validations;

    @Mock
    private GlobalPotMembersRepository globalPotMembersRepository;

    @InjectMocks
    private TransferOwnerShipGlobalPot transferOwnerShipGlobalPot;

    @Test
    void testTransferOwnershipType() {
        GlobalPotRequestType type =
                transferOwnerShipGlobalPot.getGlobalPotRequestType();

        assertEquals(GlobalPotRequestType.TRANSFER_OWNERSHIP, type);
    }

    @Test
    void testOwnershipTransferredSuccessfully() throws IOException {

        // --- Arrange ---
        User admin = User.builder()
                .uuid("admin-123")
                .build();

        User targetUser = User.builder()
                .uuid("target-123")
                .build();

        GlobalPot globalPot = GlobalPot.builder()
                .globalPotId("id123")
                .build();

        GlobalPotMembers adminMember = GlobalPotMembers.builder()
                .user(admin)
                .userRoles(UserRoles.GLOBALPOT_ADMIN)
                .globalPot(globalPot)
                .build();

        GlobalPotMembers targetMember = GlobalPotMembers.builder()
                .user(targetUser)
                .userRoles(UserRoles.NORMAL_USER)
                .globalPot(globalPot)
                .build();

        GlobalPotRequest request = GlobalPotRequest.builder()
                .globalPotId(globalPot.getGlobalPotId())
                .adminUuid("admin-123")
                .targetUserUuid("target-123")
                .build();

        when(globalPotValidations.getGlobalPot(request.getGlobalPotId()))
                .thenReturn(globalPot);

        when(validations.getUserInfo("admin-123"))
                .thenReturn(admin);

        when(validations.getUserInfo("target-123"))
                .thenReturn(targetUser);

        doNothing().when(globalPotValidations)
                .validateAdmin(admin, globalPot);

        when(globalPotValidations.getMemberByUuidAndGlobalPotId(admin, globalPot))
                .thenReturn(adminMember);

        when(globalPotValidations.getMemberByUuidAndGlobalPotId(targetUser, globalPot))
                .thenReturn(targetMember);

        when(globalPotMembersRepository.saveAll(any(List.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // --- Act ---
        GlobalPotResponse response =
                transferOwnerShipGlobalPot.perform(request);

        // --- Assert ---
        assertNotNull(response);
        assertEquals(
                "Ownership transferred successfully to user: " + targetUser.getUuid(),
                response.getMessage()
        );

        assertEquals(UserRoles.NORMAL_USER, adminMember.getUserRoles());
        assertEquals(UserRoles.GLOBALPOT_ADMIN, targetMember.getUserRoles());

        verify(globalPotMembersRepository).saveAll(any(List.class));
    }
}

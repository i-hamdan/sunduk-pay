package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.factories.GlobalPotFactory.ShareOwnershipGlobalPot;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ShareOwnershipGlobalPotTest {

    @Mock
    private GlobalPotValidations globalPotValidations;

    @Mock
    private Validations validations;

    @Mock
    private GlobalPotMembersRepository globalPotMembersRepository;

    @InjectMocks
    private ShareOwnershipGlobalPot shareOwnershipGlobalPot;

    @Test
    void testGlobalPotRequestType() {
        GlobalPotRequestType type =
                shareOwnershipGlobalPot.getGlobalPotRequestType();

        assertEquals(GlobalPotRequestType.SHARE_OWNERSHIP, type);
    }

    @Test
    void testOwnershipSharedSuccessfully() throws IOException {

        User admin = User.builder()
                .uuid("admin-123")
                .build();

        User targetUser = User.builder()
                .uuid("target-123")
                .build();

        GlobalPot globalPot = GlobalPot.builder()
                .globalPotId("pot-1")
                .build();

        GlobalPotMembers member = new GlobalPotMembers();

        GlobalPotRequest request = GlobalPotRequest.builder()
                .adminUuid("admin-123")
                .targetUserUuid("target-123")
                .globalPotId("pot-1")
                .build();

        when(validations.getUserInfo("admin-123"))
                .thenReturn(admin);
        when(validations.getUserInfo("target-123"))
                .thenReturn(targetUser);

        when(globalPotValidations.getGlobalPot("pot-1"))
                .thenReturn(globalPot);

        doNothing().when(globalPotValidations)
                .validateAdmin(admin, globalPot);

        when(globalPotValidations
                .getMemberByUuidAndGlobalPotId(targetUser, globalPot))
                .thenReturn(member);

        when(globalPotMembersRepository.save(member))
                .thenReturn(member);

        GlobalPotResponse response =
                shareOwnershipGlobalPot.perform(request);

        assertNotNull(response);
        assertEquals(
                "Ownership shared successfully with user:target-123",
                response.getMessage()
        );

        assertEquals(
                UserRoles.GLOBALPOT_ADMIN,
                member.getUserRoles()
        );
    }
}

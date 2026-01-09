package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.factories.GlobalPotFactory.ShareOwnershipGlobalPot;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
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

import static org.mockito.ArgumentMatchers.any;
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
    private UserRepository userRepository;

    @InjectMocks
    private ShareOwnershipGlobalPot shareOwnershipGlobalPot;

    @Test
    void testGlobalPotRequestType(){

        GlobalPotRequestType type = shareOwnershipGlobalPot.getGlobalPotRequestType();
        assertEquals(GlobalPotRequestType.SHARE_OWNERSHIP,type,
                "Should share the ownership with another user");
    }

    @Test
    void testOwnershipSharedSuccessfully() throws IOException {

        User admin = User.builder()
                .uuid("admin-123")
                .userRole(UserRoles.GLOBALPOT_ADMIN)
                .build();

        User targetUser = User.builder()
                .uuid("target-123")
                .userRole(UserRoles.NORMAL_USER)
                .build();

        GlobalPotRequest request = GlobalPotRequest.builder()
                .adminUuid("admin-123")
                .targetUserUuid("target-123")
                .build();

        when(validations.getUserInfo("admin-123"))
                .thenReturn(admin);

        when(validations.getUserInfo("target-123"))
                .thenReturn(targetUser);

        doNothing().when(globalPotValidations)
                .validateAdmin(admin);
        when(userRepository.save(any(User.class)))
                .thenReturn(targetUser);

        GlobalPotResponse response =
                shareOwnershipGlobalPot.perform(request);

        assertNotNull(response);
        assertEquals(
                "Ownership shared successfully with user:" + targetUser.getUuid(),
                response.getMessage()
        );
    }
}
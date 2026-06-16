package com.bxb.sunduk_pay.factories.userFactory;

import com.bxb.sunduk_pay.Mappers.UserMapper;
import com.bxb.sunduk_pay.encryption.MpinValidations;
import com.bxb.sunduk_pay.factories.userFactory.GetUserService;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.util.UserRequestType;
import com.bxb.sunduk_pay.validations.Validations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class GetUserServiceTest {

    @Mock
    private Validations validations;

    @Mock
    private MpinValidations mpinValidations;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private GetUserService getUserService;


    @Test
    void shouldReturnUserDetailsRequestType() {

        UserRequestType type = getUserService.getUserRequestType();

        assertEquals(UserRequestType.USER_DETAILS, type);
    }


    @Test
    void userResponseTest() {

        UserRequest request = new UserRequest();
        request.setUuid("uuid-123");
        request.setMpin("pin123");

        User user = new User();
        user.setUuid("uuid-123");

        UserResponse response = new UserResponse();

        doReturn(user)
                .when(validations)
                        .getUserInfo("uuid-123");

        doReturn(response)
                .when(userMapper)
                .getDetails(user);

        // when
        UserResponse result = getUserService.perform(request);

        // then
        assertNotNull(result);
        assertEquals(response, result);

        verify(validations).getUserInfo("uuid-123");
        verify(mpinValidations).validateMpin("uuid-123", "pin123");
        verify(userMapper).getDetails(user);

    }
}

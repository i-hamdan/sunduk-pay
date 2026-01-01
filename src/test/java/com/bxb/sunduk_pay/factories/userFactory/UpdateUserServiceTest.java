package com.bxb.sunduk_pay.factories.userFactory;

import com.bxb.sunduk_pay.Mappers.UserMapper;
import com.bxb.sunduk_pay.encryption.MpinValidations;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.util.UserRequestType;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class UpdateUserServiceTest {

    @Mock
    private Validations validations;

    @Mock
    private MpinValidations mpinValidations;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UpdateUserService updateUserService;

    @Test
    void shouldReturnUpdateUserRequestType() {
        UserRequestType type = updateUserService.getUserRequestType();
        Assertions.assertEquals(UserRequestType.UPDATE, type);
    }

    @Test
    void shouldupdateUserSuccessfully() {

        UserRequest request = new UserRequest();
        request.setUuid("uuid-0000");

        User existingUser = new User();
        existingUser.setUuid("uuid-0000");
        existingUser.setFullName("old Name");

        User updatedUser = new User();
        updatedUser.setUuid("uuid-0000");
        updatedUser.setUuid("new name");


        doReturn(existingUser)
                .when(validations)
                .getUserInfo("uuid-0000");

        doReturn(updatedUser)
                .when(userMapper)
                .toUpdate(request, existingUser);

        doReturn(updatedUser)
                .when(userRepository)
                .save(updatedUser);


        // when
        UserResponse response = updateUserService.perform(request);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals("saved new info Successfully",
                response.getMessage());


        verify(validations).getUserInfo("uuid-0000");
        verify(userMapper).toUpdate(request, existingUser);
        verify(userRepository).save(updatedUser);

    }
}

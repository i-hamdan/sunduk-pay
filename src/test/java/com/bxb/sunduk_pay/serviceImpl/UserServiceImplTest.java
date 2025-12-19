package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.UserMapper;
import com.bxb.sunduk_pay.factories.userFactory.UserOperation;
import com.bxb.sunduk_pay.factories.userFactory.UserOperationFactory;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.MasterWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.*;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.util.UserRequestType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserOperationFactory userOperationFactory;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private KafkaTemplate<String, UserKafkaEvent> kafkaTemplate;

    @Mock
    private MainWalletRepository mainWalletRepository;

    @Mock
    private MasterWalletRepository masterWalletRepository;

    @Mock
    private MpinRepository mpinRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;


    @Test
    void shouldCreateNewUserWhenUserNotExists() {

        // Given

        UserResponse userResponse = new UserResponse();
        userResponse.setEmail("email");
        userResponse.setPhoneNumber("2020202020");

        User mappedUser = new User();
        mappedUser.setEmail("email");


        UserKafkaEvent kafkaEvent = new UserKafkaEvent();

        when(userMapper.toKafkaEvent(any(User.class), eq("SIGNUP")))
                .thenReturn(kafkaEvent);

        when(userRepository.findByEmailAndIsDeletedFalse("email"))
                .thenReturn(Optional.empty());

        when(userMapper.toUser(userResponse))
                .thenReturn(mappedUser);

        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArgument(0));


        when(mainWalletRepository.save(any(MainWallet.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(masterWalletRepository.save(any(MasterWallet.class)))
                .thenAnswer(i -> i.getArgument(0));

        when(mpinRepository.findByUserUuid(anyString()))
                .thenReturn(Optional.empty());

        // when
        User user = userServiceImpl.userLogin(userResponse);


        // Then
        Assertions.assertNotNull(user);
        Assertions.assertEquals("email", user.getEmail());

        verify(kafkaTemplate)
                .send(eq("user-topic"), any(UserKafkaEvent.class));


        verify(userRepository, times(2))
                .save(any(User.class));
    }

    @Test
    void shouldLoginExistingUser() {

        // Give
        UserResponse userResponse = new UserResponse();
        userResponse.setEmail("email");

        User existingUser = new User();
        existingUser.setEmail("email");
        existingUser.setUuid("uuid");

        when(userRepository.findByEmailAndIsDeletedFalse("email"))
                .thenReturn(Optional.of(existingUser));

        when(mpinRepository.findByUserUuid("uuid"))
                .thenReturn(Optional.ofNullable(null));

        when(userMapper.toKafkaEvent(any(User.class), eq("LOGIN")))
                .thenReturn(new UserKafkaEvent());

        // when

        User user = userServiceImpl.userLogin(userResponse);

        // Then

        Assertions.assertNotNull(user);
        Assertions.assertEquals("email", user.getEmail());

        verify(kafkaTemplate)
                .send(eq("user-topic"), any(UserKafkaEvent.class));


    }


    @Test
    void shouldExecuteCorrectUserOperation() {

        UserRequest request = new UserRequest();
        request.setUserRequestType(UserRequestType.UPDATE);

        UserResponse expectedUserResponse = new UserResponse();
        expectedUserResponse.setMessage("Success");

        UserOperation mockUserOperation = mock(UserOperation.class);

        when(userOperationFactory.getUserOperations(UserRequestType.UPDATE))
                .thenReturn(mockUserOperation);

        when(mockUserOperation.perform(request))
                .thenReturn(expectedUserResponse);

        // when
        UserResponse actualResponse = userServiceImpl
                .userOperations(request);

        // than
        Assertions.assertNotNull(actualResponse);
        Assertions.assertEquals(expectedUserResponse, actualResponse);

        verify(userOperationFactory)
                .getUserOperations(UserRequestType.UPDATE);

        verify(mockUserOperation)
                .perform(request);
    }
}

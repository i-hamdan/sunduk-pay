package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.UserMapper;
import com.bxb.sunduk_pay.factories.userFactory.UserOperation;
import com.bxb.sunduk_pay.factories.userFactory.UserOperationFactory;
import com.bxb.sunduk_pay.kafkaEvents.UserKafkaEvent;
import com.bxb.sunduk_pay.model.GlobalLandingPage;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalLandingPageRepository;
import com.bxb.sunduk_pay.repository.MpinRepository;
import com.bxb.sunduk_pay.repository.UserRepository;
import com.bxb.sunduk_pay.request.UserRequest;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.util.UserRequestType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
    private MpinRepository mpinRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Mock
    private GlobalLandingPageRepository globalLandingPageRepository;

    @Test
    void shouldLoginExistingUser() {

        // Give
        UserResponse userResponse = new UserResponse();
        userResponse.setEmail("email");

        User existingUser = new User();
        existingUser.setEmail("email");
        existingUser.setUuid("uuid");

        GlobalLandingPage defaultPage = GlobalLandingPage.builder()
                .id(1L)
                .key("DEFAULT_LANDING_PAGE")
                .value("DASHBOARD")
                .build();

        when(globalLandingPageRepository.findByKey(anyString())).thenReturn(Optional.of(defaultPage));

        when(userRepository.findByEmailAndIsDeletedFalse("email")).thenReturn(Optional.of(existingUser));

        when(mpinRepository.findByUserUuid("uuid")).thenReturn(Optional.ofNullable(null));

        when(userMapper.toKafkaEvent(any(User.class), eq("LOGIN"))).thenReturn(new UserKafkaEvent());

        // when

        User user = userServiceImpl.userLogin(userResponse);

        // Then

        Assertions.assertNotNull(user);
        assertEquals("email", user.getEmail());

        verify(kafkaTemplate).send(eq("user-topic"), any(UserKafkaEvent.class));


    }


    @Test
    void shouldExecuteCorrectUserOperation() {

        UserRequest request = new UserRequest();
        request.setUserRequestType(UserRequestType.UPDATE);

        UserResponse expectedUserResponse = new UserResponse();
        expectedUserResponse.setMessage("Success");

        UserOperation mockUserOperation = mock(UserOperation.class);

        when(userOperationFactory.getUserOperations(UserRequestType.UPDATE)).thenReturn(mockUserOperation);

        when(mockUserOperation.perform(request)).thenReturn(expectedUserResponse);

        // when
        UserResponse actualResponse = userServiceImpl.userOperations(request);

        // than
        Assertions.assertNotNull(actualResponse);
        assertEquals(expectedUserResponse, actualResponse);

        verify(userOperationFactory).getUserOperations(UserRequestType.UPDATE);

        verify(mockUserOperation).perform(request);
    }
}


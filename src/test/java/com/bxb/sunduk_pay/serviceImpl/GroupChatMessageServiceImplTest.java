package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.exception.ChatProcessingException;
import com.bxb.sunduk_pay.exception.RedisOperationException;
import com.bxb.sunduk_pay.kafkaEvents.GroupChatEvent;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GroupChatMessage;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.FollowerRepository;
import com.bxb.sunduk_pay.repository.GroupChatMessageRepository;
import com.bxb.sunduk_pay.response.AnonymousIdentityDTO;
import com.bxb.sunduk_pay.response.GroupChatMessageResponse;
import com.bxb.sunduk_pay.response.UserResponse;
import com.bxb.sunduk_pay.service.AnonymousUserService;
import com.bxb.sunduk_pay.util.GenerateKeyUtil;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GroupChatMessageServiceImplTest {

    @Mock
    private GroupChatMessageRepository groupChatMessageRepository;

    @Mock
    private Validations validations;

    @Mock
    private GlobalPotValidations globalPotValidations;

    @Mock
    private GlobalPotMapper globalPotMapper;

    @Mock
    private RedisTemplate<String,
            GroupChatMessageResponse> messageResponseRedisTemplate;

    @Mock
    private GenerateKeyUtil generateKeyUtil;

    @Mock
    private FollowerRepository followerRepository;

    @Mock
    private AnonymousUserService anonymousUserService;

    @Spy
    @InjectMocks
    private GroupChatMessageServiceImpl chatMessageService;

    private GroupChatEvent getEvent() {
        return GroupChatEvent.builder()
                .senderId("sender_id")
                .globalPotId("global_pot_id")
                .content("hello")
                .isAnonymous(true)
                .build();
    }

    private User getUser() {
        return User.builder().uuid("user_id")
                .fullName("fullname")
                .email("@email.com")
                .build();
    }

    private GlobalPot getGlobalPot() {
        return GlobalPot.builder()
                .globalPotId("global_pot_id")
                .caseTitle("pot_title")
                .build();
    }

    public AnonymousIdentityDTO getAnonymousDetails() {
        return new AnonymousIdentityDTO(
                "Anonymous_id", "#FF5733");
    }

    public GroupChatMessageResponse getGroupChatMessageResponse() {
        return GroupChatMessageResponse.builder()
                .messageId("message_id")
                .sender(UserResponse.builder().build())
                .globalPotId(getGlobalPot().getGlobalPotId())
                .content(getEvent().getContent())
                .timestamp(Instant.now().toString())
                .isAnonymous(getEvent().isAnonymous())
                .anonymousId("Anonymous_id")
                .anonymousColor("#FF5733")
                .build();
    }

    public GroupChatMessage getGroupChatMessage() {
    return GroupChatMessage.builder()
            .messageId("message_id")
            .sender(getUser())
            .globalPot(getGlobalPot())
            .content("hello there!")
            .timestamp(Instant.now())
            .isAnonymous(true)
            .anonymousColor("#FF5733")
            .anonymousId("Anonymous_id")
            .build();
    }

        @Test
        void processGroupChatMessageTest () {

            GroupChatEvent event = getEvent();

            User user = getUser();

            GlobalPot globalPot = getGlobalPot();

            AnonymousIdentityDTO anonymousUser = getAnonymousDetails();

            when(validations.getUserInfo(anyString())).thenReturn(user);

            when(globalPotValidations.getGlobalPot(anyString())).thenReturn(globalPot);

            when(followerRepository.existsByFollowerUserAndGlobalPot(any(), any())).thenReturn(true);

            when(anonymousUserService.getOrCreateAnonymousColor(any(), any())).thenReturn(anonymousUser);


            GroupChatMessageResponse response1 = getGroupChatMessageResponse();

            when(globalPotMapper.toGroupChatMessageResponse(any())).thenReturn(response1);

            doNothing().when(chatMessageService).saveMessageInRedisCache(any());

            GroupChatMessageResponse response = chatMessageService
                    .processGroupChatMessage(getEvent());
            assertNotNull(response);

            verify(groupChatMessageRepository, times(1)).save(any());

        }

    @Test
    void testCaseWithNewFollower () {

        GroupChatEvent event = getEvent();

        User user = getUser();

        GlobalPot globalPot = getGlobalPot();

        AnonymousIdentityDTO anonymousUser = getAnonymousDetails();

        when(validations.getUserInfo(anyString())).thenReturn(user);

        when(globalPotValidations.getGlobalPot(anyString())).thenReturn(globalPot);

        when(followerRepository.existsByFollowerUserAndGlobalPot(any(), any()))
                .thenReturn(false);

//        doNothing().when(chatMessageService).createFollowerRecord(any(),any());

        when(anonymousUserService.getOrCreateAnonymousColor(any(), any())).thenReturn(anonymousUser);


        GroupChatMessageResponse response1 = getGroupChatMessageResponse();

        when(globalPotMapper.toGroupChatMessageResponse(any())).thenReturn(response1);

        doNothing().when(chatMessageService).saveMessageInRedisCache(any());

        GroupChatMessageResponse response = chatMessageService
                .processGroupChatMessage(getEvent());
        assertNotNull(response);

        verify(groupChatMessageRepository, times(1)).save(any());

    }

    @Test
    void processGroupChatMessage_shouldThrowChatProcessingException() {

        // Arrange
        GroupChatEvent event = getEvent();

        when(validations.getUserInfo(anyString()))
                .thenThrow(new RuntimeException("DB down"));

        // Act + Assert
        ChatProcessingException exception =
                assertThrows(ChatProcessingException.class,
                        () -> chatMessageService.processGroupChatMessage(event));

        assertTrue(exception.getMessage()
                .contains("Error processing group chat message"));

        verify(groupChatMessageRepository, never()).save(any());
        verify(globalPotMapper, never()).toGroupChatMessageResponse(any());
    }


    @Test
    void testSaveMessageInRedisCache() {

        // Arrange
        ListOperations listOperations = mock(ListOperations.class);

        when(messageResponseRedisTemplate.hasKey(any()))
                .thenReturn(true);

        when(messageResponseRedisTemplate.opsForList())
                .thenReturn(listOperations);

        when(listOperations.rightPush(any(), any()))
                .thenReturn(1L);

        // Act
        chatMessageService.saveMessageInRedisCache(getGroupChatMessage());

        // Assert
        verify(messageResponseRedisTemplate).hasKey(any());
        verify(listOperations).rightPush(any(), any());
    }


    @Test
    void testSaveMessageInRedisCacheWithKeyNotPresent() {

        // Arrange
        ListOperations listOperations = mock(ListOperations.class);

        when(messageResponseRedisTemplate.hasKey(any()))
                .thenReturn(false);

        when(groupChatMessageRepository.findByGlobalPotGlobalPotId(
                anyString())).thenReturn(
                List.of(GroupChatMessage.builder()
                        .messageId("123").build())
        );


        when(messageResponseRedisTemplate.opsForList())
                .thenReturn(listOperations);

        when(listOperations.rightPush(any(), any()))
                .thenReturn(1L);

        // Act
        chatMessageService.saveMessageInRedisCache(getGroupChatMessage());

        // Assert
        verify(messageResponseRedisTemplate).hasKey(any());
        verify(listOperations).rightPush(any(), any());


    }


    @Test
    void negativeTestSaveMessageInRedis(){

        // Arrange
        ListOperations listOperations = mock(ListOperations.class);

        when(messageResponseRedisTemplate.hasKey(any()))
                .thenReturn(false);

        when(groupChatMessageRepository.findByGlobalPotGlobalPotId(
                anyString())).thenReturn(
                List.of(GroupChatMessage.builder()
                        .messageId("123").build())
        );


        when(messageResponseRedisTemplate.opsForList())
                .thenThrow(new RuntimeException("Unexpected Redis error"));

        assertThrows(RedisOperationException.class,
                () -> chatMessageService.saveMessageInRedisCache(
                        getGroupChatMessage()));
    }




}


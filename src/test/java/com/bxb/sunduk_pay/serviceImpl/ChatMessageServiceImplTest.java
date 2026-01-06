package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.ChatMessageMapper;
import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.exception.RedisOperationException;
import com.bxb.sunduk_pay.kafkaEvents.ChatMessageEvent;
import com.bxb.sunduk_pay.model.ChatMessage;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.ChatMessageRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.response.ChatMessageResponse;
import com.bxb.sunduk_pay.response.TransactionResponse;
import com.bxb.sunduk_pay.util.GenerateKeyUtil;
import com.bxb.sunduk_pay.validations.MessageValidations;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChatMessageServiceImplTest {

    @Mock
    private RedisTemplate<String, ChatMessage> chatMessageRedisTemplate;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private Validations validations;

    @Mock
    private MessageValidations messageValidations;

    @Mock
    private GenerateKeyUtil generateKeyUtil;

    @Mock
    private ChatMessageMapper chatMessageMapper;

    @Mock
    private ListOperations<String, ChatMessage> listOperations;

    @InjectMocks
    private ChatMessageServiceImpl chatMessageService;

    private ChatMessageEvent chatMessageEvent() {
        return ChatMessageEvent.builder()
                .senderId("sender-uuid")
                .receiverId("receiver-uuid")
                .content("Hello")
                .build();
    }

    private ChatMessage chatMessage() {
        return ChatMessage.builder()
                .senderId("sender-uuid")
                .receiverId("receiver-uuid")
                .content("Hello")
                .status("SUCCESS")
                .build();
    }

    private User user(){
        return User.builder().uuid(UUID.randomUUID().toString())
                .phoneNumber("9856468489").build();
    }


    @Test
    void processMessage_success() {
        // Arrange
        ChatMessageEvent event = chatMessageEvent();
        ChatMessage chatMessage = chatMessage();
        User user = user();

        String redisKey = "chat:sender-uuid:receiver-uuid";

        when(chatMessageMapper.toChatMessage(event)).thenReturn(chatMessage);
        when(generateKeyUtil.generateChatKey(
                chatMessage.getSenderId(),
                chatMessage.getReceiverId()))
                .thenReturn(redisKey);

        when(chatMessageRedisTemplate.hasKey(redisKey)).thenReturn(true);
        when(chatMessageRedisTemplate.opsForList())
                .thenReturn(listOperations);

        when(validations.getUserInfo(anyString())).thenReturn(user);

        when(chatMessageMapper.toChatMessageResponse(
                any(ChatMessage.class),
                anyString(),
                anyString()))
                .thenReturn(new ChatMessageResponse());

        // Act
        ChatMessageResponse response =
                chatMessageService.processMessage(event);

        // Assert
        assertNotNull(response);

        verify(chatMessageMapper).toChatMessage(event);
        verify(chatMessageRepository).save(any(ChatMessage.class));
        verify(listOperations).rightPush(eq(redisKey), any(ChatMessage.class));
        verify(chatMessageRedisTemplate).expire(eq(redisKey), any());
    }

    @Test
    void saveMessage_success() {
        // Arrange
        ChatMessage message = chatMessage();
        String redisKey = "chat:sender-uuid:receiver-uuid";

        when(generateKeyUtil.generateChatKey(
                message.getSenderId(),
                message.getReceiverId()))
                .thenReturn(redisKey);

        when(chatMessageRedisTemplate.hasKey(redisKey)).thenReturn(true);
        when(chatMessageRedisTemplate.opsForList())
                .thenReturn(listOperations);

        // Act
        chatMessageService.saveMessage(message);

        // Assert
        verify(listOperations).rightPush(redisKey, message);
        verify(chatMessageRedisTemplate).expire(eq(redisKey), any());
    }

    @Test
    void saveMessage_keyNotExists_createsNewKey() {
        // Arrange
        ChatMessage newMessage = chatMessage();
        String redisKey = "chat:sender-uuid:receiver-uuid";

        // Sample old messages from DB
        List<ChatMessage> dbMessages = List.of(
                new ChatMessage(),
                new ChatMessage()
        );

        when(generateKeyUtil.generateChatKey(
                newMessage.getSenderId(),
                newMessage.getReceiverId()))
                .thenReturn(redisKey);

        when(chatMessageRedisTemplate.hasKey(redisKey)).thenReturn(false);
        when(messageValidations.getMessagesFromDb(anyString(), anyString()))
                .thenReturn(dbMessages);

        when(chatMessageRedisTemplate.opsForList()).thenReturn(listOperations);

        // Act
        chatMessageService.saveMessage(newMessage);

        verify(listOperations).rightPushAll(redisKey, dbMessages);

        verify(listOperations).rightPush(redisKey, newMessage);

        verify(chatMessageRedisTemplate)
                .expire(eq(redisKey), any(Duration.class));

        verify(messageValidations).getMessagesFromDb(
                newMessage.getSenderId(), newMessage.getReceiverId()
        );
    }


    @Test
    void saveMessage_redisFailure_throwsException() {
        ChatMessage message = chatMessage();
        String redisKey = "chat:sender-uuid:receiver-uuid";

        when(generateKeyUtil.generateChatKey(
                message.getSenderId(),
                message.getReceiverId()))
                .thenReturn(redisKey);

        when(chatMessageRedisTemplate.hasKey(redisKey))
                .thenThrow(new RuntimeException("Redis down"));

        assertThrows(RedisOperationException.class,
                () -> chatMessageService.saveMessage(message));
    }



}
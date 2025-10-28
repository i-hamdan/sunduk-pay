package com.bxb.sunduk_pay.kafkaEvents;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageEvent {
    /** ID of the sender of the message */
    private String senderId;
    /** ID of the receiver of the message */
    private String receiverId;
    /** Content of the chat message */
    private String content;
}

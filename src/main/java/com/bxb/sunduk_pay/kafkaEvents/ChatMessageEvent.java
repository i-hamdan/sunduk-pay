package com.bxb.sunduk_pay.kafkaEvents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * Event class representing a chat message.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEvent {
    /** ID of the sender of the message. */
    private String senderId;
    /** ID of the receiver of the message. */
    private String receiverId;
    /** Content of the chat message. */
    private String content;
}

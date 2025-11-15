package com.bxb.sunduk_pay.kafkaEvents;

import lombok.*;
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

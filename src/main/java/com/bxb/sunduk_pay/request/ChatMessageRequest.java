package com.bxb.sunduk_pay.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request object for sending a chat message.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
    public class ChatMessageRequest {
    /** The ID of the sender of the message. */
    private String senderId;
    /** The ID of the receiver of the message. */
    private String receiverId;
    /** The content of the chat message. */
    private String content;

    /** Indicates if the sender is blocked by the receiver. */
    private Boolean isBlocked;
    }


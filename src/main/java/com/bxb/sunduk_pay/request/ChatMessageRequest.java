package com.bxb.sunduk_pay.request;

import lombok.Data;

/**
 * Request object for sending a chat message.
 */
@Data
    public class ChatMessageRequest {
    /** The ID of the sender of the message. */
    private String senderId;
    /** The ID of the receiver of the message. */
    private String receiverId;
    /** The content of the chat message. */
    private String content;
    }


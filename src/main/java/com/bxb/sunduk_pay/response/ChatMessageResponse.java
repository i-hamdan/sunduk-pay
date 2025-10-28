package com.bxb.sunduk_pay.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {
    /** Unique identifier for the chat message */
    private String messageId;
    /** ID of the sender of the message */
    private String senderId;
    /** ID of the receiver of the message */
    private String receiverId;
    /** Content of the chat message */
    private String content;
    /** Status of the chat message*/
    private String status;
    /** Timestamp when the message was sent */
    private LocalDateTime timestamp;
}

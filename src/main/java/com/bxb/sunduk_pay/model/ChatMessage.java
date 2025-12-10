package com.bxb.sunduk_pay.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a chat message exchanged between users.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    /**
     * Unique identifier for the chat message.
     */
    @Id
    private String messageId;
    /**
     * ID of the sender of the message.
     */
    private String senderId;
    /**
     * ID of the receiver of the message.
     */
    private String receiverId;
    /**
     * Content of the chat message.
     */
    private String content;
    /**
     * Timestamp when the message was sent.
     */
    private LocalDateTime timestamp;
    /**
     * Status of the chat message.
     */
    private String status;

}

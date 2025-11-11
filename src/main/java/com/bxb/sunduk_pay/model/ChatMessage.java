package com.bxb.sunduk_pay.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

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

    /**
     * Associated transaction, if the message is related to a transaction.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = true)
    private Transaction transaction;
}

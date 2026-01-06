package com.bxb.sunduk_pay.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Represents a message sent in a group chat within a GlobalPot.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "group_chat_messages")
public class GroupChatMessage {

    /**
     * Unique identifier for the group chat message.
     * Generated automatically using UUID strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String messageId;

    /**
     * Reference to the sender user of the message.
     */
    @ManyToOne
    @JoinColumn(name = "sender_user_id", nullable = false)
    private User sender;

    /**
     * Reference to the GlobalPot (group) where the message is sent.
     */
    @ManyToOne
    @JoinColumn(name = "global_pot_id", nullable = false)
    private GlobalPot globalPot;

    /**
     * Content of the group chat message.
     */
    private String content;

    /**
     * Timestamp when the message was sent.
     */
    private Instant timestamp;

    /**
     * Indicates if the sender is anonymous.
     */
    private boolean isAnonymous;

    /**
     * Identifier for the anonymous sender.
     */
    private String anonymousId;
    /**
     * Color associated with the anonymous sender.
     */
    private String anonymousColor;
}

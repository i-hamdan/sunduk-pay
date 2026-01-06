package com.bxb.sunduk_pay.request;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Request DTO for sending a group chat message.
 */
@Getter
@Setter
public class GroupChatMessageRequest {
    /**
     * Unique identifier for the sender user of the message.
     */
    private String senderId;
    /**
     * Unique identifier for the GlobalPot (group) where the message is sent.
     */
    private String globalPotId;
    /**
     * Content of the group chat message.
     */
    private String content;

    /** Indicates if the message is sent anonymously. */
    @NonNull
    private Boolean isAnonymous;
}

package com.bxb.sunduk_pay.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Response object for group chat messages.
 */
@Getter
@Setter
@Builder
public class GroupChatMessageResponse {

    /** Unique identifier for the message. */
    private String messageId;

    /** Identifier of the sender user. */
    private UserResponse sender;

    /** Identifier of the GlobalPot (group). */
    private String globalPotId;

    /** Content of the message. */
    private String content;

    /** Timestamp when the message was sent. */
    private String timestamp;
}

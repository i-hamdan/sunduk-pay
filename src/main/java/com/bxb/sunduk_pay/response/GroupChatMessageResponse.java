package com.bxb.sunduk_pay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * Response object for group chat messages.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    /** Indicates if the sender is anonymous. */
    private Boolean isAnonymous;

    /** Identifier for the anonymous sender. */
    private String anonymousId;

    /** Color associated with the anonymous sender. */
    private String anonymousColor;
}

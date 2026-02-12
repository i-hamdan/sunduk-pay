package com.bxb.sunduk_pay.kafkaEvents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Event object for group chat messages.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupChatEvent {

    /** The ID of the sender of the message. */
    private String senderId;

    /** The global pot ID associated with the group chat. */
    private String globalPotId;

    /** The content of the group chat message. */
    private String content;

    /** Indicates if the message was sent anonymously. */
    private boolean isAnonymous;

    /** The path of the image attached to the group chat message, if any. */
    private String imagePath;

    /** Flag indicating if the message contains an image. */
    private Boolean isImage;
}

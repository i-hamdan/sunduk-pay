package com.bxb.sunduk_pay.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {
    /** Unique identifier for the chat message. */
    private String messageId;
    /** ID of the sender of the message. */
    private String senderId;
    /** ID of the receiver of the message. */
    private String receiverId;
    /** PhoneNumber of the sender ,
     * including in response dto because of frontend requirement.*/
    private String senderPhoneNumber;
    /** PhoneNumber of the receiver ,
     * including in response dto because of frontend requirement.*/
    private String receiverPhoneNumber;
    /** Content of the chat message. */
    private String content;
    /** Status of the chat message.*/
    private String status;
    /** Timestamp when the message was sent. */
    private String date;
    /** Time when the message was sent. */
    private String time;
    /** Indicates if the sender is blocked by the receiver. */
    private Boolean isBlocked;
    /** The path of the image attached to the chat message, if any. */
    private Boolean isImage;
    /** The path of the image attached to the chat message, if any. */
    private String imagePath;

}

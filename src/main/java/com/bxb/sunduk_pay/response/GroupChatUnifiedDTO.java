package com.bxb.sunduk_pay.response;

import com.bxb.sunduk_pay.util.ChatDtoDataType;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

/**
 * A unified Data Transfer Object (DTO) for group chat-related data.
 * This DTO encapsulates various types of data that can be sent or received
 * in a group chat context, along with metadata
 * such as the data type and timestamp.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupChatUnifiedDTO {
    /** The type of data contained in the DTO. */
    private ChatDtoDataType dataType;
    /** The timestamp of the event or message. */
    private String timestamp;
    /** The actual data payload. */
    private Object data;
}

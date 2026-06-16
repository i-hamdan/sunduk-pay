package com.bxb.sunduk_pay.response;

import com.bxb.sunduk_pay.util.ChatDtoDataType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Represents a unified data transfer object combining both
 * chat messages and transaction details in a single structure.
 */
@Getter
@Setter
@Builder
public class ChatAndTransactionUnifiedDTO {

    /** Specifies the type of data — either "CHAT" or "TRANSACTION". */
    private ChatDtoDataType type;

    /** The timestamp indicating when the chat or transaction occurred. */
    private LocalDateTime dateTime;

    /** Holds the actual chat message or transaction response object. */
    private Object data;
}

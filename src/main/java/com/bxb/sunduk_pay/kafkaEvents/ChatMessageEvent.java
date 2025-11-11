package com.bxb.sunduk_pay.kafkaEvents;

import com.bxb.sunduk_pay.model.Transaction;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageEvent {
    /** ID of the sender of the message */
    private String senderId;
    /** ID of the receiver of the message */
    private String receiverId;
    /** Content of the chat message */
    private String content;
//    /** Timestamp when the message was sent */
//    private boolean isTransaction;
//    /** The amount involved in the transaction, if applicable */
//    private Double amount;
//   /** Wallet ID of the sender */
//    private String senderWalletId;
//    /** Transaction ID associated with the message, if applicable */
//    private String transactionId;
}

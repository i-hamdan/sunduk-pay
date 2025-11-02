package com.bxb.sunduk_pay.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ChatMessage {
    @Id
    private String messageId;
    private String senderId;
    private String receiverId;
    private String content;
    private LocalDateTime timestamp;
    private String status;
    private String kafkaTopic;
}

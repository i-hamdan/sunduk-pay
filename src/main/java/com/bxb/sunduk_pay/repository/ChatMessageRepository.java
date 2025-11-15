package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends
        JpaRepository <ChatMessage, String> {

    /**
     * Finds chat messages between a sender and receiver,
     * ordered by timestamp in descending order.
     * @param senderId   the ID of the sender
     * @param receiverId the ID of the receiver
     * @return a list of chat messages between the sender and receiver
     */
    @Query("""
            SELECT m FROM ChatMessage m WHERE
            (m.senderId = :senderId AND m.receiverId = :receiverId)
            OR (m.senderId = :receiverId AND m.receiverId = :senderId)
            ORDER BY m.timestamp DESC
            """)
    List<ChatMessage> findBySenderIdAndReceiverId(
            String senderId, String receiverId);
}

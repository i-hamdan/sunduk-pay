package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends
        JpaRepository<ChatMessage,String> {
}

package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.Mappers.ChatMessageMapper;
import com.bxb.sunduk_pay.exception.CannotFetchMessagesException;
import com.bxb.sunduk_pay.model.ChatMessage;
import com.bxb.sunduk_pay.repository.ChatMessageRepository;
import com.bxb.sunduk_pay.response.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implementation of MessageValidations for validating and processing chat messages.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class MesageValidationsImpl implements MessageValidations {

    /**
     * Mapper for converting chat message models to response DTOs.
     */
    private final ChatMessageMapper chatMessageMapper;

    /**
     * Repository for chat message data access.
     */
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public List<ChatMessage> getMessagesFromDb(
            final String senderId,
            final String receiverId) {
        try {
            if (senderId==null || receiverId==null) {
                throw new IllegalArgumentException(
                        "Sender ID and Receiver ID " +
                        "must not be null.");
            }
            return chatMessageRepository
                    .findBySenderIdAndReceiverId(
                            senderId,
                            receiverId);
        } catch (Exception e) {
            log.error("Error retrieving messages from DB: "
                    + e.getMessage());
            throw new CannotFetchMessagesException(
                    "Unable to fetch messages from " +
                    "database." + e.getMessage());
        }

    }

}

package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.GroupChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupChatMessageRepository
        extends JpaRepository<GroupChatMessage, String> {

    /**
     * Finds all group chat messages associated with a specific global pot ID.
     * @param globalPotId the ID of the global pot
     * @return a list of group chat messages linked to the
     * specified global pot ID
     */
    List<GroupChatMessage> findByGlobalPotGlobalPotId(String globalPotId);
}

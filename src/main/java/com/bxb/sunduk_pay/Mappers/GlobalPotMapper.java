package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.factories.GlobalPotFactory.GlobalPotTileDto;
import com.bxb.sunduk_pay.kafkaEvents.GroupChatEvent;
import com.bxb.sunduk_pay.model.Contributor;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalWallet;
import com.bxb.sunduk_pay.model.GroupChatMessage;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.request.GroupChatMessageRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.response.GroupChatMessageResponse;
import com.fasterxml.jackson.databind.ser.Serializers;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

/**
 * Mapper interface for converting between GlobalPot-related entities and DTOs.
 */
public interface GlobalPotMapper {

    /**
     * Converts the incoming request into a persistence-ready Entity.
     */
    GlobalPot toEntity(GlobalPotRequest request) throws IOException;

    /**
     * Converts a GlobalPot entity to a GlobalPotResponse DTO.
     */
    GlobalPotResponse toGlobalPotResponse(GlobalPot pot);

    /**
     * Converts a GlobalPotRequest to a GlobalWallet entity.
     */
    GlobalWallet toEntityWallet(final GlobalPotRequest request);

    /**
     * Converts a GlobalPotRequest to a Contributor entity.
     */
    Contributor toContributerEntity(GlobalPotRequest request, GlobalPot pot)
            throws IOException;

    /**
     * Converts a GlobalPot entity to a GlobalPotTileDto.
     */
    GlobalPotTileDto toTileDto(GlobalPot pot);

    /**
     * Converts a list of GlobalPot entities to a list of GlobalPotTileDto.
     */
    List<GlobalPotTileDto> toTileDtos(List<GlobalPot> pots);

    /**
     * Converts a GroupChatMessageRequest to a GroupChatEvent.
     *
     * @param request the group chat message request
     * @return the corresponding group chat event
     */
    GroupChatEvent toGroupChatEvent(GroupChatMessageRequest request);

    /**
     * Converts a GroupChatMessage entity to a GroupChatMessageResponse DTO.
     *
     * @param groupChatMessage the group chat message entity
     * @return the corresponding group chat message response
     */
    GroupChatMessageResponse toGroupChatMessageResponse(
            GroupChatMessage groupChatMessage);

    List<GroupChatMessageResponse> toGroupChatMessageResponseList(
            List<GroupChatMessage> groupChatMessage);

    String toBase64(byte[] data);
}

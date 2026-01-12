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

import java.io.IOException;
import java.util.List;

/**
 * Mapper interface for converting between GlobalPot-related entities and DTOs.
 */
public interface GlobalPotMapper {

    /**
     * Converts the incoming request into a persistence-ready Entity.
     *
     * @param request the GlobalPotRequest containing pot details
     * @return the corresponding GlobalPot entity
     * @throws IOException if an error occurs during conversion
     */
    GlobalPot toEntity(GlobalPotRequest request) throws IOException;

    /**
     * Converts a GlobalPot entity to a GlobalPotResponse DTO.
     *
     * @param pot the GlobalPot entity
     * @return the corresponding GlobalPotResponse DTO
     */
    GlobalPotResponse toGlobalPotResponse(GlobalPot pot);

    /**
     * Converts a GlobalPotRequest to a GlobalWallet entity.
     *
     * @param request the GlobalPotRequest containing wallet details
     * @return the corresponding GlobalWallet entity
     */
    GlobalWallet toEntityWallet(GlobalPotRequest request);

    /**
     * Converts a GlobalPotRequest to a Contributor entity.
     *
     * @param request the GlobalPotRequest containing contributor details
     * @param pot     the associated GlobalPot entity
     * @return the corresponding Contributor entity
     * @throws IOException if an error occurs during conversion
     */
    Contributor toContributerEntity(GlobalPotRequest request, GlobalPot pot)
            throws IOException;

    /**
     * Converts a GlobalPot entity to a GlobalPotTileDto.
     *
     * @param pot the GlobalPot entity
     * @return the corresponding GlobalPotTileDto
     */
    GlobalPotTileDto toTileDto(GlobalPot pot);

    /**
     * Converts a list of GlobalPot entities to a list of GlobalPotTileDto.
     *
     * @param pots the list of GlobalPot entities
     * @return list of GlobalPotTileDto
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

    /**
     * Converts a list of GroupChatMessage entities to a list of
     * GroupChatMessageResponse DTOs.
     * @param groupChatMessage the list of group chat message entities
     * @return list of GroupChatMessageResponse
     */
    List<GroupChatMessageResponse> toGroupChatMessageResponseList(
            List<GroupChatMessage> groupChatMessage);

    /**
     * Encodes the given byte array to a Base64 string.
     *
     * @param data the byte array to encode
     * @return the Base64 encoded string
     */
    String toBase64(byte[] data);
}

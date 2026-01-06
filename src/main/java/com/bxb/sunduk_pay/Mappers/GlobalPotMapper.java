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
     * Converts a GlobalPotRequest to a GlobalPot entity.
     *
     * @param request the global pot request
     * @return the corresponding global pot entity
     * @throws IOException if an I/O error occurs during conversion
     */
    GlobalPot  toEntity(GlobalPotRequest request) throws IOException;


    /**
     * Converts a GlobalPot entity to a GlobalPotResponse DTO.
     *
     * @param pot the global pot entity
     * @return the corresponding global pot response DTO
     */
    GlobalPotResponse toGlobalPotResponse(GlobalPot pot);


    /**     * Converts a GlobalPotRequest to a GlobalWallet entity.
     *
     * @param request the global pot request
     * @return the corresponding global wallet entity
     */
    GlobalWallet toEntityWallet(final GlobalPotRequest request);



    /**     * Converts a GlobalPotRequest to a Contributor entity.
     *
     * @param request the global pot request
     * @param pot the global pot entity
     * @return the corresponding contributor entity
     * @throws IOException if an I/O error occurs during conversion
     */
    Contributor toContributerEntity(GlobalPotRequest request, GlobalPot pot) throws IOException;


/**
     * Converts a GlobalPot entity to a GlobalPotTileDto.
     *
     * @param pot the global pot entity
     * @return the corresponding global pot tile DTO
     */
    GlobalPotTileDto toTileDto(GlobalPot pot);

    /**
     * Converts a list of GlobalPot entities to a list of GlobalPotTileDto.
     *
     * @param pots the list of global pot entities
     * @return the corresponding list of global pot tile DTOs
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
     * @return the corresponding group chat message response DTO
     */
    GroupChatMessageResponse toGroupChatMessageResponse(
            GroupChatMessage groupChatMessage);

    /**
     * Converts a GroupChatMessage entity to a GroupChatMessageResponse DTO.
     *
     * @param groupChatMessage the group chat message entity
     * @return the corresponding group chat message response DTO
     */
    List<GroupChatMessageResponse> toGroupChatMessageResponseList(
            List<GroupChatMessage> groupChatMessage);

     String toBase64(byte[] image);
}

package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.GlobalPotMembers;
import com.bxb.sunduk_pay.response.FetchGlobalMembersDTO;
/**
 * Mapper interface for converting GlobalPotMembers entities to FetchGlobalMembersDTOs.
 */
public interface GlobalPotMemberMapper {
/**
     * Maps a GlobalPotMembers entity to a FetchGlobalMembersDTO.
     *
     * @param globalPotMember the GlobalPotMembers entity to be mapped
     * @return the corresponding FetchGlobalMembersDTO
     */
    FetchGlobalMembersDTO toFetchGlobalMembersDTO(
            GlobalPotMembers globalPotMember);
}

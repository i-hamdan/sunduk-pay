package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.GlobalPotMembers;
import com.bxb.sunduk_pay.response.FetchGlobalMembersDTO;
import org.springframework.stereotype.Component;
/**
 * Implementation of the GlobalPotMemberMapper interface for mapping
 * GlobalPotMembers entities to FetchGlobalMembersDTOs.
 */
@Component
public class GlobalPotMemberMapperImpl implements GlobalPotMemberMapper{

    /**
     * Maps a GlobalPotMembers entity to a FetchGlobalMembersDTO.
     *
     * @param member the GlobalPotMembers entity to be mapped
     * @return the corresponding FetchGlobalMembersDTO
     */
    @Override
    public FetchGlobalMembersDTO toFetchGlobalMembersDTO(GlobalPotMembers member) {
        return FetchGlobalMembersDTO.builder()
                .uuid(member.getUser().getUuid())
                .fullName(member.getUser().getFullName())
                .role(member.getUserRoles().toString())
                .build();
    }
}


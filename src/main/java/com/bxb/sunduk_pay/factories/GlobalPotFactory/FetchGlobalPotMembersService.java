package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMemberMapper;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalPotMembers;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.GlobalPotMembersRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.FetchGlobalMembersDTO;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotMemberFilter;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.UserRoles;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bouncycastle.oer.Switch;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Service responsible for fetching members of a Global Pot.
 *
 * Admins and members are allowed to view the member list.
 */
@Service
@AllArgsConstructor
@Log4j2
public class FetchGlobalPotMembersService implements GlobalPotOperation {

    /** Validations utility. */
    private final Validations validations;
    /** Global Pot validations utility. */
    private final GlobalPotValidations globalPotValidations;
    /** Repository for Global Pot members. */
    private final GlobalPotMembersRepository globalPotMembersRepository;
    /** Mapper for Global Pot members. */
    private final GlobalPotMemberMapper globalPotMemberMapper;

    /**
     * Identifies the type of request this service handles.
     *
     * @return GlobalPotRequestType.FETCH_GLOBAL_POT_MEMBERS
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.FETCH_GLOBAL_POT_MEMBERS;
    }
/**
     * Performs the operation to fetch members of a Global Pot.
     *
     * @param request The request containing user UUID and Global Pot ID.
     * @return GlobalPotResponse containing the list of members.
     * @throws IOException If an I/O error occurs during processing.
     */
    @Override
    public GlobalPotResponse perform(
            final GlobalPotRequest request) throws IOException {

 log.info("FetchGlobalPotMembers started | userUuid={} globalPotId={}",
                request.getUuid(),
                request.getGlobalPotId());


        User requester = validations.getUserInfo(request.getUuid());
 log.info("Requester fetched | userUuid={}", requester.getUuid());

        GlobalPot globalPot =
                globalPotValidations.getGlobalPot(request.getGlobalPotId());
        log.info("GlobalPot fetched | globalPotId={}",
                globalPot.getGlobalPotId());


        List<GlobalPotMembers> members;

        switch(request.getGlobalPotMemberFilter()){
            case MEMBER_ONLY -> members = globalPotMembersRepository
                    .findByGlobalPotGlobalPotIdAndUserRoles(
                            globalPot.getGlobalPotId(),
                            UserRoles.NORMAL_USER);

            case ADMIN_ONLY -> members = globalPotMembersRepository
                    .findByGlobalPotGlobalPotIdAndUserRoles(
                            globalPot.getGlobalPotId(),
                            UserRoles.GLOBALPOT_ADMIN);

            default -> members = globalPotMembersRepository
                    .findByGlobalPotGlobalPotId(globalPot.getGlobalPotId());
        }


        List<FetchGlobalMembersDTO> memberDtos =
        members.stream()
                .map(globalPotMemberMapper::toFetchGlobalMembersDTO)
                .collect(Collectors.toList());

log.info(
    "Members fetched successfully | count={}",
        memberDtos.size());

        return GlobalPotResponse.builder()
                .status("SUCCESS")
                .message("Global Pot members fetched successfully")
                .fetchGlobalMembersDTOS(memberDtos)
                .build();
    }

}


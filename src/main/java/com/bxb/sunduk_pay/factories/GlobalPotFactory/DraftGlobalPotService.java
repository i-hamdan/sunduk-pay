package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.PotStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
/**
 * Service for drafting a Global Pot.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DraftGlobalPotService implements GlobalPotOperation {

    /** Mapper for converting between Global Pot entities and requests. */
    private final GlobalPotMapper mapper;
    /** Repository for Global Pot entities. */
    private final GlobalPotRepository globalPotRepository;
    /**
     * Returns the type of Global Pot request this service handles.
     *
     * @return the Global Pot request type
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.DRAFT_POT;
    }
    /**
     * Performs the drafting of a Global Pot.
     *
     * @param request the Global Pot request
     * @return the response indicating the result of the operation
     * @throws IOException if an I/O error occurs
     */
    @Override
    public GlobalPotResponse perform(final GlobalPotRequest request)
            throws IOException {

        log.info("Global Pot draft initiated.");
        GlobalPot globalPot = mapper.toEntity(request);

        globalPot.setPotStatus(PotStatus.DRAFT);
        globalPotRepository.save(globalPot);
        log.info("Global Pot drafted with ID: {}", globalPot.getGlobalPotId());

        return GlobalPotResponse.builder()
                .message("Global Pot drafted successfully.")
                .build();
    }
}

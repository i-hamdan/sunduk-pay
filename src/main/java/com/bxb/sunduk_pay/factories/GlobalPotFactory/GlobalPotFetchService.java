package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GlobalPotFetchService implements GlobalPotOperation {

    private final GlobalPotRepository globalPotRepository;

    private final GlobalPotMapper globalPotMapper;

    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.FETCH_GLOBAL_POT;
    }

    @Override
    public GlobalPotResponse perform(GlobalPotRequest request) {

        List<GlobalPot>pots = List.of();

    if (request.getCaseCategory() == null ||
        request.getCaseCategory() == CaseCategory.ALL){

        pots = globalPotRepository.findByPotScope(PotScope.PUBLIC);
    }
    else {
        globalPotRepository.findByCaseCategoryAndPotScope(
                request.getCaseCategory(),
                PotScope.PUBLIC
        );
    }
        List<GlobalPotTileDto> tileDtos = globalPotMapper.toTileDtos(pots);

    return GlobalPotResponse.builder()
                .status("SUCCESS")
                .message("global pots fetched successfully")
                .globalPotsTiles(tileDtos)
                .build();
    }
}

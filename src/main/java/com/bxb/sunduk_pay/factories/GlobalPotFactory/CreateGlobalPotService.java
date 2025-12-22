package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.Mappers.GlobalPotMapperImpl;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateGlobalPotService implements GlobalPotOperation{

    private final GlobalPotMapper mapper;
    private final GlobalPotRepository repository;

    @Autowired
    public CreateGlobalPotService(GlobalPotMapperImpl mapper, GlobalPotRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }


    /**
     * @return
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.CREATE_POT;
    }

    /**
     * @param request
     * @return
     */
    @Override
    @Transactional
    public GlobalPotResponse perform(GlobalPotRequest request) {
        GlobalPot newPot=mapper.toEntity(request);
        GlobalPot savedPot=repository.save(newPot);
        return mapper.toResponse(savedPot);
    }
}

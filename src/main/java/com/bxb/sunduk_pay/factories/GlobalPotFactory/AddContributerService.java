package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.model.Contributor;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.repository.ContributerRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddContributerService implements GlobalPotOperation{

    private final ContributerRepository contributerRepository;
    private final GlobalPotValidations globalPotValidations;
    private final GlobalPotMapper globalPotMapper;

    public AddContributerService(ContributerRepository contributerRepository, GlobalPotValidations globalPotValidations, GlobalPotMapper globalPotMapper) {
        this.contributerRepository = contributerRepository;
        this.globalPotValidations = globalPotValidations;
        this.globalPotMapper = globalPotMapper;
    }
    /**
     * @return
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.ADD_CONTRIBUTER;
    }

    /**
     * @param request
     * @return
     */
    @Override
    @Transactional
    public GlobalPotResponse perform(GlobalPotRequest request) {
        GlobalPot globalPot=
                globalPotValidations.validateGlobalPot(request.getGlobalPotId());
        Contributor contributor =globalPotMapper.toContributerEntity(request,
                globalPot);
        contributerRepository.save(contributor);
        return globalPotMapper.toResponse();
    }
}

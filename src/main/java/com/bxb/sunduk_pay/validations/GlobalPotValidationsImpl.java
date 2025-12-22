package com.bxb.sunduk_pay.validations;

import com.bxb.sunduk_pay.exception.GlobalPotNotFoundException;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class GlobalPotValidationsImpl implements GlobalPotValidations{

    private final GlobalPotRepository globalPotRepository;

    /**
     * @param globalPotId
     */
    @Override
    public GlobalPot validateGlobalPot(String globalPotId) {
        GlobalPot globalPot =
                globalPotRepository.findById(globalPotId).orElseThrow(
                        ()-> new GlobalPotNotFoundException(
                                "Global Pot not found with ID: "
                                        + globalPotId));
        return globalPot;

    }
}

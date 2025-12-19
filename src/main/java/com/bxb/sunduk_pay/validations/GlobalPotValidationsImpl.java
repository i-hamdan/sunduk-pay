//package com.bxb.sunduk_pay.validations;
//
//import com.bxb.sunduk_pay.model.GlobalPot;
//import com.bxb.sunduk_pay.repository.GlobalPotRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.stereotype.Component;
//
//import java.util.Optional;
//
//@Log4j2
//@Component
//@RequiredArgsConstructor
//public class GlobalPotValidationsImpl implements GlobalPotValidations{
//
//    private final GlobalPotRepository globalPotRepository;
//
//    /**
//     * @param globalPotId
//     */
//    @Override
//    public void validateIsVerified(String globalPotId) {
//        Optional<GlobalPot> globalPot = globalPotRepository.findById(globalPotId);
//        if (globalPot.get().getIsVerified()){
//            log.info("Global Pot is verified for Global Pot ID: {}",
//                    globalPotId);
//        }
//        else {
//            log.error("Global Pot is not verified for Global Pot ID: {}",
//                    globalPotId);
//            throw new IllegalStateException("Global Pot is not verified.");
//        }
//    }
//}

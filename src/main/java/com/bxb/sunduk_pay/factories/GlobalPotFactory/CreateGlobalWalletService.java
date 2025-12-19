package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalWallet;
import com.bxb.sunduk_pay.repository.GlobalWalletRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import org.springframework.stereotype.Service;

@Service
public class CreateGlobalWalletService implements GlobalPotOperation {

    private final GlobalWalletRepository globalWalletRepository;
//    private final GlobalPotValidations globalPotValidations;
    private final GlobalPotMapper globalPotMapper;

    public CreateGlobalWalletService(GlobalWalletRepository globalWalletRepository, GlobalPotMapper globalPotMapper) {
        this.globalWalletRepository = globalWalletRepository;
        this.globalPotMapper = globalPotMapper;
    }


    /**
     * @return
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.CREATE_WALLET;
    }

    /**
     * @param request
     * @return
     */
    @Override
    public GlobalPotResponse perform(GlobalPotRequest request) {
//        globalPotValidations.validateIsVerified(request.getGlobalPotId());
        GlobalWallet entityWallet = globalPotMapper.toEntityWallet(request);
        globalWalletRepository.save(entityWallet);
        return globalPotMapper.toResponse(entityWallet);
    }
}

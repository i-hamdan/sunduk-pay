package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalWallet;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.repository.GlobalWalletRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateGlobalWalletService implements GlobalPotOperation {

    private final GlobalWalletRepository globalWalletRepository;
    private final GlobalPotValidations globalPotValidations;
    private final GlobalPotMapper globalPotMapper;
    private final GlobalPotRepository globalPotRepository;

    public CreateGlobalWalletService(
            GlobalWalletRepository globalWalletRepository,
            GlobalPotValidations globalPotValidations,
            GlobalPotMapper globalPotMapper,
            GlobalPotRepository globalPotRepository
    ) {
        this.globalWalletRepository = globalWalletRepository;
        this.globalPotValidations = globalPotValidations;
        this.globalPotMapper = globalPotMapper;
        this.globalPotRepository = globalPotRepository;
    }


    /**
     * Specifies the type of Global Pot request this service handles.
     *
     * @return the GlobalPotRequestType for verifying or creating a Global Wallet
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.VERIFY_POT;
    }

    /**
     * Ensures that a Global Wallet exists for the specified Global Pot.
     * If it does not exist, a new Global Wallet is created and linked to the Pot.
     *
     * @param request the request containing the Global Pot ID
     * @return a response indicating the result of the operation
     */
    @Override
    @Transactional
    public GlobalPotResponse perform(GlobalPotRequest request) {

        // 1. Fetch and Validate the Pot existence
        GlobalPot globalPot = globalPotValidations.getGlobalPot(request.getGlobalPotId());

        GlobalWallet globalWallet = new GlobalWallet();

        // 2. Logic: Ensure a wallet is created or retrieved
        if (globalPot.getGlobalWallet() == null) {
            // Transform request to Entity and set defaults
            globalWallet = globalPotMapper.toEntityWallet(request);

            // Set Bidirectional Link (Wallet -> Pot)
            globalWallet.setGlobalPot(globalPot);

            // Save Wallet first to generate ID
            globalWallet = globalWalletRepository.save(globalWallet);

            // Update Pot state (Pot -> Wallet & Status)
            globalPot.setGlobalWallet(globalWallet);
            globalPotRepository.save(globalPot);
        }

        // 3. Return the specialized Wallet Response
        return GlobalPotResponse.builder().message(
                "Global Wallet verified/created successfully").status("SUCCESS").build();
    }


}

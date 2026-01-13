package com.bxb.sunduk_pay.factories.adminFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalWallet;
import com.bxb.sunduk_pay.repository.GlobalPotRepository;
import com.bxb.sunduk_pay.repository.GlobalWalletRepository;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.request.SundukPayAdminRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.response.SundukPayAdminResponse;
import com.bxb.sunduk_pay.util.AdminRequestType;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class VerifyGlobalPot implements SundukPayAdminOperation{


    /** Repository for Global Wallet operations. */
    private final GlobalWalletRepository globalWalletRepository;
    /** Validations for Global Pot operations. */
    private final GlobalPotValidations globalPotValidations;
    /** Mapper for Global Pot data transformations. */
    private final GlobalPotMapper globalPotMapper;
    /** Repository for Global Pot operations. */
    private final GlobalPotRepository globalPotRepository;


    @Override
    public AdminRequestType getAdminRequestType() {
        return AdminRequestType.VERIFY_POT;
    }

    @Override
    public SundukPayAdminResponse perform(SundukPayAdminRequest request) {
        // 1. Fetch and Validate the Pot existence
        GlobalPot globalPot = globalPotValidations
                .getGlobalPot(request.getGlobalPotId());

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
        return SundukPayAdminResponse.builder().message(
                        "Global Wallet verified/created successfully")
                .status("SUCCESS").build();
    }

}

package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.response.SubWalletResponse;

import java.util.List;

/**
 * Mapper interface for converting MainWallet and SubWallet entities
 * into response DTOs for API responses.
 */
public interface WalletMapper {
    /**
     * Converts a MainWallet entity and its
     * associated SubWallets
     * into a MainWalletResponse DTO.
     *
     * @param wallet the main wallet entity
     * @param subWallets the list of sub-wallets
     * @return a MainWalletResponse representing the main wallet
     *         and its sub-wallets
     */
     MainWalletResponse toWalletResponse(MainWallet wallet,
                                         List<SubWallet> subWallets);


     SubWalletResponse toSubWalletResponse(SubWallet subWallet);
}


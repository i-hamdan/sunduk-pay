package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.response.SubWalletResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of WalletMapper to convert MainWallet and SubWallet entities
 * into corresponding response DTOs.
 */
@Component
public  class WalletMapperImpl implements WalletMapper {

    /**
     * Converts a MainWallet entity and its associated SubWallets into
     * a MainWalletResponse DTO.
     *
     * @param wallet     the main wallet entity
     * @param subWallets the list of sub-wallets
     * @return a MainWalletResponse containing main wallet and sub-wallets
     */
    @Override
    public MainWalletResponse toWalletResponse(
            final MainWallet wallet,
            final List<SubWallet> subWallets
    ) {
        MainWalletResponse response = new MainWalletResponse();
        response.setMainWalletId(wallet.getMainWalletId());
        response.setBalance(wallet.getBalance());
        response.setUuid(wallet.getUser().getUuid());
        response.setSubWallets(toSubWalletResponseList(subWallets));
        return response;
    }

    /**
     * Converts a list of SubWallet entities into SubWalletResponse DTOs.
     *
     * @param subWallets the list of sub-wallets
     * @return a list of SubWalletResponse DTOs
     */
    private List<SubWalletResponse> toSubWalletResponseList(
            final List<SubWallet> subWallets
    ) {
        List<SubWalletResponse> responses = new ArrayList<>();
        for (SubWallet subWallet : subWallets) {
            responses.add(toSubWalletResponse(subWallet));
        }
        return responses;
    }

    /**
     * Converts a single SubWallet entity into a SubWalletResponse DTO.
     *
     * @param subWallet the sub-wallet entity
     * @return the corresponding SubWalletResponse DTO
     */
    private SubWalletResponse toSubWalletResponse(final SubWallet subWallet) {
        SubWalletResponse response = new SubWalletResponse();
        response.setSubWalletId(subWallet.getSubWalletId());
        response.setSubWalletName(subWallet.getSubWalletName());
        response.setBalance(subWallet.getBalance());
        response.setTargetBalance(subWallet.getTargetBalance());
        response.setIcon(subWallet.getIcon());
        return response;
    }
}

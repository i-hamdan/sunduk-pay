package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.Mappers.WalletMapper;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to fetch main wallet and its active sub-wallets for a user.
 * Service to fetch the main wallet and active sub-wallets for a user.
 */
@Service
public class FetchWalletService implements WalletOperation{
    private final Validations validations;
    private final WalletMapper walletMapper;

    public FetchWalletService(Validations validations, WalletMapper walletMapper) {
        this.validations = validations;
        this.walletMapper = walletMapper;
    }

    /**
     * Returns the request type handled by this service.
     *
     * @return request type FETCH_WALLET
     */
    @Override
    public RequestType getRequestType() {
        return RequestType.FETCH_WALLET;
    }

    /**
     * Fetches main wallet and non-deleted
     * sub-wallets for a given user UUID.
     * Fetches main wallet and active (non-deleted)-
     * -sub-wallets for a given user UUID.
     * @param mainWalletRequest request containing the user UUID
     * @return response containing wallet and sub-wallet details
     */
    @Override
    public MainWalletResponse perform(MainWalletRequest mainWalletRequest) {
        MainWallet mainWallet = validations.getMainWalletInfo(mainWalletRequest.getUuid());
        List<SubWallet> subWallets = mainWallet.getSubWallets().stream().filter
                (sw -> !sw.getIsDeleted()).toList();
        return walletMapper.toWalletResponse(mainWallet,subWallets);
    }
}

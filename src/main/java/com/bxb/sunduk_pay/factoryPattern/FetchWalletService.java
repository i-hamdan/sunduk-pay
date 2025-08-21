package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.Mappers.WalletMapper;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.MasterWallet;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import org.springframework.stereotype.Service;

@Service
public class FetchWalletService implements WalletOperation{
    private final Validations validations;
    private final WalletMapper walletMapper;

    public FetchWalletService(Validations validations, WalletMapper walletMapper) {
        this.validations = validations;
        this.walletMapper = walletMapper;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.FETCH_WALLET;
    }

    @Override
    public MainWalletResponse perform(MainWalletRequest mainWalletRequest) {
        MainWallet mainWallet = validations.getMainWalletInfo(mainWalletRequest.getUuid());
        return walletMapper.toWalletResponse(mainWallet);
    }
}

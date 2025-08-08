package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.request.WalletRequest;
import com.bxb.sunduk_pay.response.WalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import org.springframework.stereotype.Service;


@Service
public class Fetch implements WalletOperation {
    @Override
    public RequestType getRequestType() {

        return RequestType.FETCH;
    }

    @Override
    public WalletResponse perform(WalletRequest walletRequest) {
        return null;
    }


}

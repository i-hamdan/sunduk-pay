package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.request.WalletRequest;
import com.bxb.sunduk_pay.response.WalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import org.springframework.stereotype.Service;


@Service
public class Delete implements WalletOperation {
    @Override
    public RequestType getRequestType() {
        return RequestType.DELETE;
    }

    @Override
    public WalletResponse perform(WalletRequest walletRequest) {
        return null;
    }

}

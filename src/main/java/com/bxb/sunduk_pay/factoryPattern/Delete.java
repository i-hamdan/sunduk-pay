package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import org.springframework.stereotype.Service;


@Service
public class Delete implements WalletOperation {
    @Override
    public RequestType getRequestType() {
        return RequestType.DELETE;
    }

    @Override
    public MainWalletResponse perform(MainWalletRequest mainWalletRequest) {
        return null;
    }

}

package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.request.WalletRequest;
import com.bxb.sunduk_pay.response.WalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class Create implements WalletOperation {


    @Override
    public RequestType getRequestType() {
        return RequestType.CREATE;
    }

    @Override
    public WalletResponse perform(WalletRequest walletRequest) {
        return null;
    }


}

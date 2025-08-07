package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.util.RequestType;
import org.springframework.stereotype.Service;


@Service
public class Get implements WalletOperation {
    @Override
    public RequestType getRequestType() {

        return RequestType.GET;
    }


}

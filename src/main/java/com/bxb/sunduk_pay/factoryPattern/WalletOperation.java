package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;

public interface WalletOperation {
RequestType getRequestType();
MainWalletResponse perform(MainWalletRequest mainWalletRequest);
}

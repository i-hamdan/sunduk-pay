package com.bxb.sunduk_pay.factoryPattern;

import com.bxb.sunduk_pay.request.WalletRequest;
import com.bxb.sunduk_pay.response.WalletResponse;
import com.bxb.sunduk_pay.util.RequestType;

public interface WalletOperation {
RequestType getRequestType();
WalletResponse perform(WalletRequest walletRequest);
}

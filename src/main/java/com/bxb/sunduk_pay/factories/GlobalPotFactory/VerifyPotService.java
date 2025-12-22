package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;

public class VerifyPotService implements GlobalPotOperation{
    /**
     * @return
     */
    @Override
    public GlobalPotRequestType getGlobalPotRequestType() {
        return GlobalPotRequestType.VERIFY_POT;
    }

    /**
     * @param request
     * @return
     */
    @Override
    public GlobalPotResponse perform(GlobalPotRequest request) {

        return null;
    }
}

package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.util.RequestType;
import org.bouncycastle.asn1.ocsp.Request;

public interface GlobalPotOperation {


    GlobalPotRequestType getGlobalPotRequestType();

    GlobalPotResponse perform(GlobalPotRequest request);
}

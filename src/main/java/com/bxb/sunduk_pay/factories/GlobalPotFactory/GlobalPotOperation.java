package com.bxb.sunduk_pay.factories.GlobalPotFactory;

import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import java.io.IOException;

public interface GlobalPotOperation {


    /**
     * Returns the type of Global Pot request
     * that this operation handles.
     *
     * @return the Global Pot request type
     */
    GlobalPotRequestType getGlobalPotRequestType();

    /**
     * Performs the operation based on the provided
     * Global Pot request.
     *
     * @param request the Global Pot request
     * @return the response after performing the operation
     * @throws IOException if an I/O error occurs
     */
    GlobalPotResponse perform(GlobalPotRequest request) throws IOException;
}

package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalWallet;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;

public interface GlobalPotMapper {

    GlobalPot toEntity(GlobalPotRequest request);
    void updateEntity(GlobalPot pot, GlobalPotRequest request);
    GlobalPotRequest toRequest(GlobalPot pot);
    GlobalPotResponse toResponse(GlobalPot pot);
    GlobalPotResponse toResponse(GlobalWallet wallet);
    GlobalWallet toEntityWallet(final GlobalPotRequest request);
}

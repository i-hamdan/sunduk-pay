package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.model.Contributor;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalWallet;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;

import java.io.IOException;

public interface GlobalPotMapper {

    GlobalPot toEntity(GlobalPotRequest request) throws IOException;
    void updateEntity(GlobalPot pot, GlobalPotRequest request);
    GlobalPotRequest toRequest(GlobalPot pot);
    GlobalPotResponse toResponse(GlobalPot pot);
    GlobalPotResponse toResponse(GlobalWallet wallet);
    GlobalWallet toEntityWallet(final GlobalPotRequest request);
    Contributor toContributerEntity(GlobalPotRequest request, GlobalPot pot);
    GlobalPotResponse toResponse();
}

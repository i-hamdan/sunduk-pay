package com.bxb.sunduk_pay.Mappers;

import com.bxb.sunduk_pay.factories.GlobalPotFactory.GlobalPotTileDto;
import com.bxb.sunduk_pay.model.Contributor;
import com.bxb.sunduk_pay.model.GlobalPot;
import com.bxb.sunduk_pay.model.GlobalWallet;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;

import java.io.IOException;
import java.util.List;

public interface GlobalPotMapper {

    GlobalPot toEntity(GlobalPotRequest request) throws IOException;


    GlobalPotResponse toGlobalPotResponse(GlobalPot pot);


    GlobalWallet toEntityWallet(final GlobalPotRequest request);



    Contributor toContributerEntity(GlobalPotRequest request, GlobalPot pot) throws IOException;



    GlobalPotTileDto toTileDto(GlobalPot pot);

  List<GlobalPotTileDto> toTileDtos(List<GlobalPot> pots);



}

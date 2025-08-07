package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.model.Wallet;
import com.bxb.sunduk_pay.response.WalletResponse;

;

public interface WalletMapper {
     WalletResponse toWalletResponse(Wallet wallet);

}

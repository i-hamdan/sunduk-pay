package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.response.MainWalletResponse;

;

public interface WalletMapper {
     MainWalletResponse toWalletResponse(MainWallet wallet);

}

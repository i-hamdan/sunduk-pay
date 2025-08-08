package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.response.WalletResponse;

;

public interface WalletMapper {
     WalletResponse toWalletResponse(MainWallet wallet);
     WalletResponse toTransferResponse(SubWallet sourceWallet, SubWallet targetWallet, Double amount);

}

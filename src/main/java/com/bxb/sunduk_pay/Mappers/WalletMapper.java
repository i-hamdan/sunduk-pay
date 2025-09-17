package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.response.MainWalletResponse;

import java.util.List;

public interface WalletMapper {
     MainWalletResponse toWalletResponse(MainWallet wallet, List<SubWallet> subWallets);

}

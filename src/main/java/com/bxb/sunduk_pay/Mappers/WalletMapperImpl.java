package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.response.SubWalletResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of WalletMapper to convert MainWallet and SubWallet entities
 * into corresponding response DTOs.
 */
@Component
public class WalletMapperImpl implements WalletMapper{

    /** {@inheritDoc} */
    public MainWalletResponse toWalletResponse(MainWallet wallet,List<SubWallet> subWallets){
       MainWalletResponse mainWalletResponse = new MainWalletResponse();
       mainWalletResponse.setMainWalletId(wallet.getMainWalletId());
       mainWalletResponse.setBalance(wallet.getBalance());
       mainWalletResponse.setUuid(wallet.getUser().getUuid());
       mainWalletResponse.setSubWallets(toSubWalletResponseList(subWallets));
       return mainWalletResponse;
   }

    /** Supporting method for converting list of subWallets into list of subWalletResponse*/
    private List<SubWalletResponse> toSubWalletResponseList(List<SubWallet> subWallet){
       List<SubWalletResponse> list = new ArrayList<>();
       for (SubWallet subWallet1 : subWallet){
           list.add(toSubWalletResponse(subWallet1));
       }
       return list;
   }
    /** Supporting method for converting subWallet into subWalletResponse*/
   private SubWalletResponse toSubWalletResponse(SubWallet subWallet){
       SubWalletResponse subWalletResponse=new SubWalletResponse();
       subWalletResponse.setSubWalletId(subWallet.getSubWalletId());
       subWalletResponse.setSubWalletName(subWallet.getSubWalletName());
       subWalletResponse.setBalance(subWallet.getBalance());
       subWalletResponse.setTargetBalance(subWallet.getTargetBalance());
       subWalletResponse.setIcon(subWallet.getIcon());
       return subWalletResponse;
   }


}
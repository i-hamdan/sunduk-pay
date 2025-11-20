package com.bxb.sunduk_pay.Mappers;


import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.response.SubWalletResponse;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of WalletMapper to convert MainWallet and SubWallet entities
 * into corresponding response DTOs.
 */
@Component
public class WalletMapperImpl implements WalletMapper {

    /**
     * Converts a MainWallet entity and its associated SubWallets into
     * a MainWalletResponse DTO.
     * @param wallet     the main wallet entity
     * @param subWallets the list of sub-wallets
     * @return a MainWalletResponse containing main wallet and sub-wallets.
     */
    @Override
    public MainWalletResponse toWalletResponse(
            final MainWallet wallet,
            final List<SubWallet> subWallets) {
        MainWalletResponse response = new MainWalletResponse();
        response.setMainWalletId(wallet.getMainWalletId());
        response.setBalance(wallet.getBalance());
        response.setUuid(wallet.getUser().getUuid());
        response.setSubWallets(toSubWalletResponseList(subWallets));
        return response;
    }

    @Override
    public SubWalletResponse toSubWalletResponses(SubWallet subWallet) {
      SubWalletResponse subWalletResponse = new SubWalletResponse();
        subWalletResponse.setSubWalletName(subWallet.getSubWalletName());
        subWalletResponse.setBalance(subWallet.getBalance());
        subWalletResponse.setTargetBalance(subWallet.getTargetBalance());
        subWalletResponse.setTargetDate(subWallet.getTargetDate().toString());
        subWalletResponse.setIcon(subWallet.getIcon());
        subWalletResponse.setInvested(subWallet.getIsInvested());

        return subWalletResponse;
    }


    /**
     * Converts a list of SubWallet entities into
     * a list of SubWalletResponse DTOs.
     * @param subWallet the list of SubWallet entities
     * @return list of SubWalletResponse DTOs
     */
    private List<SubWalletResponse> toSubWalletResponseList(
            final List<SubWallet> subWallet) {
       List<SubWalletResponse> list = new ArrayList<>();
       for (SubWallet subWallet1 : subWallet) {
           list.add(toSubWalletResponse(subWallet1));
       }
       return list;
   }
    /**
     * Converts a SubWallet entity into a SubWalletResponse DTO.
     *
     * @param subWallet the SubWallet entity
     * @return a SubWalletResponse containing the mapped fields
     */
   private SubWalletResponse toSubWalletResponse(
           final SubWallet subWallet) {
       DateTimeFormatter formatter = DateTimeFormatter
               .ofPattern("dd MMM yyyy");
       SubWalletResponse subWalletResponse = new SubWalletResponse();
       subWalletResponse.setSubWalletId(subWallet.getSubWalletId());
       subWalletResponse.setSubWalletName(subWallet.getSubWalletName());
       subWalletResponse.setBalance(subWallet.getBalance());
       subWalletResponse.setTargetBalance(subWallet.getTargetBalance());
       subWalletResponse.setTargetDate(subWallet.getTargetDate()
               .format(formatter));
       subWalletResponse.setIcon(subWallet.getIcon());
       subWalletResponse.setCreatedAt(subWallet.getCreatedAt()
               .format(formatter));
       return subWalletResponse;
   }
}

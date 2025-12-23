package com.bxb.sunduk_pay.factories.WalletFactory;

import com.bxb.sunduk_pay.Mappers.WalletMapper;
import com.bxb.sunduk_pay.exception.WalletNotFoundException;
import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.repository.SubWalletRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.response.SubWalletResponse;
import com.bxb.sunduk_pay.util.RiskLevel;
import com.bxb.sunduk_pay.validations.InvestmentValidation;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static com.bxb.sunduk_pay.util.RequestType.FETCH_WALLET;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FetchWalletServiceTest {

    @Mock
    private Validations validations;

    @Mock
    private WalletMapper walletMapper;

    @Mock
    private InvestmentValidation investmentValidation;

    @Mock
    private SubWalletRepository subWalletRepository;

    @InjectMocks
    private FetchWalletService fetchWalletService;

    @Test
    public void fetchWalletTest(){


        MainWalletRequest request = MainWalletRequest.builder()
                .uuid(UUID.randomUUID().toString())
                .requestType(FETCH_WALLET)
                .build();

        MainWallet mainWallet = MainWallet.builder().mainWalletId(UUID.randomUUID().toString()).build();

        List<SubWallet> subWallets = List.of(
                SubWallet.builder().subWalletId(UUID.randomUUID().toString()).isInvested(false).build(),
                SubWallet.builder().subWalletId(UUID.randomUUID().toString()).isInvested(true).build()
        );

        List<SubWalletResponse> subWalletResponses = List.of(
                SubWalletResponse.builder().subWalletId(subWallets.get(0).getSubWalletId()).isInvested(subWallets.get(0).getIsInvested()).build(),
                SubWalletResponse.builder().subWalletId(subWallets.get(1).getSubWalletId()).isInvested(subWallets.get(1).getIsInvested()).build()
        );

        Investment inv = Investment.builder().profitLossPercentage(0d).riskLevel(RiskLevel.MEDIUM).build();

        when(validations.getMainWalletInfo(anyString())).thenReturn(mainWallet);
        when(subWalletRepository.findAllByMainWalletMainWalletIdAndIsDeletedFalse(
                mainWallet.getMainWalletId())).thenReturn(subWallets);
        when(walletMapper.toSubWalletResponseList(subWallets))
                .thenReturn(subWalletResponses);
        when(investmentValidation.getInvestmentBySubWalletId(anyString())).thenReturn(inv);
        when(walletMapper.toWalletResponse(eq(mainWallet), anyList()))
                .thenReturn(MainWalletResponse.builder().message("Subwallets fetched successfully").build());



        MainWalletResponse response = fetchWalletService.perform(request);

    }

    @Test
    public void fetchSubwalletWithoutMainWalletTest(){
        MainWalletRequest request = MainWalletRequest.builder()
                .uuid(UUID.randomUUID().toString())
                .requestType(FETCH_WALLET)
                .build();
        when(validations.getMainWalletInfo(anyString())).thenThrow(
                new WalletNotFoundException("Wallet not found"));


        assertThrows(WalletNotFoundException.class,()->
                fetchWalletService.perform(request));
    }



}
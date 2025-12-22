package com.bxb.sunduk_pay.factories.investmentFactory;

import com.bxb.sunduk_pay.factories.InvestmentFactory.CancelInvestmentService;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.InvestmentRepository;
import com.bxb.sunduk_pay.repository.SubWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.InvestmentRequest;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import com.bxb.sunduk_pay.util.InvestmentRequestType;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CancelInvestmentServiceTest {

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private Validations validations;

    @Mock
    private SubWalletRepository subWalletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CancelInvestmentService cancelInvestmentService;


    @Test
    void shouldgetInvestmentRequestType(){
        InvestmentRequestType type = cancelInvestmentService.getInvestmentRequestType();
        Assertions.assertEquals(type,cancelInvestmentService.getInvestmentRequestType());
    }


    @Test
    void shouldCancelInvestmentSuccessfully(){

        // given
        InvestmentRequest investmentRequest = new InvestmentRequest();
        investmentRequest.setUuid("uuid-123");
        investmentRequest.setSubWalletId("sub-001");

        User user = new User();
        MainWallet mainWallet = new MainWallet();
        mainWallet.setMainWalletId("main-001");
        user.setMainWallet(mainWallet);


        SubWallet subWallet = new SubWallet();
        subWallet.setSubWalletId("sub-001");
        subWallet.setSubWalletName("Gold Pot");
        subWallet.setBalance(1000.0);
        subWallet.setIsInvested(true);

        Investment investment = new Investment();
        investment.setActive(true);

        when(validations.getUserInfo("uuid-123"))
                .thenReturn(user);

        when(validations.findSubWalletIfExists("main-001","sub-001"))
                .thenReturn(subWallet);

        when(investmentRepository
                .findBySubWalletSubWalletIdAndIsActiveTrue("sub-001"))
                .thenReturn(Optional.of(investment));

        // when
        InvestmentResponse response =
                cancelInvestmentService.perform(investmentRequest);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "Investment cancelled successfully for pot Gold Pot",
                response.getMessage());

        verify(subWalletRepository).save(subWallet);
        verify(transactionRepository).save(any(Transaction.class));
        verify(investmentRepository).save(investment);
    }
}

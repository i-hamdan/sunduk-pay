//package com.bxb.sunduk_pay.factories.investmentFactory;
//
//import com.bxb.sunduk_pay.exception.InvestmentException;
//import com.bxb.sunduk_pay.exception.UserNotFoundException;
//import com.bxb.sunduk_pay.factories.InvestmentFactory.CancelInvestmentService;
//import com.bxb.sunduk_pay.model.*;
//import com.bxb.sunduk_pay.repository.InvestmentRepository;
//import com.bxb.sunduk_pay.repository.SubWalletRepository;
//import com.bxb.sunduk_pay.repository.TransactionRepository;
//import com.bxb.sunduk_pay.request.InvestmentRequest;
//import com.bxb.sunduk_pay.response.InvestmentResponse;
//import com.bxb.sunduk_pay.util.InvestmentRequestType;
//import com.bxb.sunduk_pay.validations.Validations;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class CancelInvestmentServiceTest {
//
//    @Mock
//    private InvestmentRepository investmentRepository;
//
//    @Mock
//    private Validations validations;
//
//    @Mock
//    private SubWalletRepository subWalletRepository;
//
//    @Mock
//    private TransactionRepository transactionRepository;
//
//    @InjectMocks
//    private CancelInvestmentService cancelInvestmentService;
//
//    //Positive testing of methods.
//
//    @Test
//    void testGetInvestmentCancellationRequest(){
//        InvestmentRequestType type = cancelInvestmentService.getInvestmentRequestType();
//
//        assertEquals(InvestmentRequestType.CANCEL_INVESTMENT,type,
//                "CancelInvestment should cancel the INVESTMENT.");
//    }
//    @Test
//    void testInvestmentCancellation(){
//        String uuid = UUID.randomUUID().toString();
//        String subWalletId = UUID.randomUUID().toString();
//        String mainWalletId = UUID.randomUUID().toString();
//
//        InvestmentRequest request = InvestmentRequest.builder().uuid(uuid)
//                .subWalletId(subWalletId).build();
//
//        MainWallet mainWallet = MainWallet.builder().mainWalletId(mainWalletId).build();
//
//        User user = User.builder().uuid(uuid).mainWallet(mainWallet).build();
//
//        SubWallet subWallet = SubWallet.builder().subWalletId(subWalletId).subWalletName("Umrah")
//                .balance(1000.0).isInvested(true).mainWallet(mainWallet)
//                .build();
//
//        Investment investment = Investment.builder().isActive(true).subWallet(subWallet).build();
//
//        when(validations.getUserInfo(uuid)).thenReturn(user);
//        when(validations.findSubWalletIfExists(mainWalletId, subWalletId)).thenReturn(subWallet);
//        when(investmentRepository.findBySubWalletSubWalletIdAndIsActiveTrue(subWalletId))
//                .thenReturn(Optional.of(investment));
//
//        InvestmentResponse response = cancelInvestmentService.perform(request);
//
//        assertNotNull(response);
//        assertEquals(
//                "Investment cancelled successfully for pot Umrah",
//                response.getMessage());
//
//        assertFalse(investment.isActive());
//        assertFalse(subWallet.getIsInvested());
//
//        verify(subWalletRepository).save(subWallet);
//        verify(transactionRepository).save(any(Transaction.class));
//        verify(investmentRepository).save(investment);
//    }
//
//    // Negative testing of method (Handling Exception).
//
//    @Test
//    void testUserNotFoundException(){
//        String uuid = UUID.randomUUID().toString();
//
//        String subWalletId = UUID.randomUUID().toString();
//
//        InvestmentRequest request = InvestmentRequest.builder().uuid(uuid)
//                .subWalletId(subWalletId).build();
//
//
//        when(validations.getUserInfo(uuid))
//                .thenThrow(new UserNotFoundException("User not found"));
//
//        InvestmentException ex = assertThrows(InvestmentException.class,
//                () -> cancelInvestmentService.perform(request));
//
//        assertTrue(ex.getMessage().contains("User not found"));
//
//    }
//
//
//    @Test
//    void testInvestmentNotFoundException (){
//
//        String uuid = UUID.randomUUID().toString();
//        String subWalletId = UUID.randomUUID().toString();
//        String mainWalletId = UUID.randomUUID().toString();
//
//        InvestmentRequest request = InvestmentRequest.builder().uuid(uuid)
//                .subWalletId(subWalletId).build();
//
//        MainWallet mainWallet = MainWallet.builder().mainWalletId(mainWalletId).build();
//
//        User user = User.builder().uuid(uuid).mainWallet(mainWallet).build();
//
//        SubWallet subWallet = SubWallet.builder().subWalletId(subWalletId).subWalletName("Umrah")
//                .balance(1000.0).isInvested(true).mainWallet(mainWallet)
//                .build();
//
//        when(validations.getUserInfo(uuid)).thenReturn(user);
//        when(validations.findSubWalletIfExists(mainWalletId, subWalletId)).thenReturn(subWallet);
//        when(investmentRepository.findBySubWalletSubWalletIdAndIsActiveTrue(subWalletId))
//                .thenReturn(Optional.empty());
//
//        InvestmentException exception = assertThrows(InvestmentException.class,
//                ()->cancelInvestmentService.perform(request));
//
//        assertTrue(exception.getMessage().contains("Investment not found"));
//
//    }
//
//}
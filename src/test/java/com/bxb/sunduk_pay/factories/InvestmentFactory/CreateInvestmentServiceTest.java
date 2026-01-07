package com.bxb.sunduk_pay.factories.investmentFactory;

import com.bxb.sunduk_pay.factories.InvestmentFactory.CreateInvestmentService;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import com.bxb.sunduk_pay.postgress.model.Units;
import com.bxb.sunduk_pay.repository.InvestmentRepository;
import com.bxb.sunduk_pay.repository.SubWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.InvestmentRequest;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import com.bxb.sunduk_pay.util.InvestmentRequestType;
import com.bxb.sunduk_pay.util.RiskLevel;
import com.bxb.sunduk_pay.validations.InvestmentValidation;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.mockito.quality.*;

import javax.sound.sampled.Port;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CreateInvestmentServiceTest {

    @Mock
    private Validations validations;

    @Mock
    private InvestmentValidation investmentValidation;

    @Mock
    private SubWalletRepository subWalletRepository;

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CreateInvestmentService createInvestmentService;

    private InvestmentRequest request;
    private SubWallet subWallet;
    private User user;
    private Units units;
    private PortfolioModel portfolioModel;

    private static final String UUID = "User-1";

    @BeforeEach
    void setup(){

         request = mock(InvestmentRequest.class);
         user = new User();
         units = new Units();
         subWallet = new SubWallet();
         portfolioModel = new PortfolioModel();
         MainWallet mainWallet = new MainWallet();

         /* note : all the values we 'get' in main class we will 'set' those values manually in test class because in main class we will get those values
         from database but in test class we use mock DB so we set those values manually */

        when(request.getUuid()).thenReturn(UUID);
        when(request.getSubWalletId()).thenReturn("SW-1");
        when(request.getRiskLevel()).thenReturn(RiskLevel.MEDIUM);

        mainWallet.setMainWalletId("MW-1");
        user.setMainWallet(mainWallet);

        subWallet.setSubWalletId("SW-1");
        subWallet.setBalance(10000.0);
        subWallet.setIsInvested(false);
        subWallet.setSubWalletName("Savings");

        portfolioModel.setId(1L);
        portfolioModel.setName("Moderate");

        units.setCombinedValue(BigDecimal.valueOf(100));

    }

    /** We check negative test cases to determine whether the system fails 'correctly' in an incorrect(error) situation or not.
     * Mission failed successfully
     * 1. The top line creates the failure.
     * 2. The line below that confirms that the failure occurred.
     * 3. The last line confirms that the subsequent work did not happen */


    @Test
    void perform_WhenUserNotFoundException_throwsException(){

        //rule: when a method has a return type we use when(..) and then for example getUserInfo has a return type

        when(validations.getUserInfo(UUID))                                             /// 1. Creates failure
                .thenThrow(new RuntimeException("User not found"));

        assertThrows((RuntimeException.class),                                         /// 2. Confirms the failure occurred
                () -> createInvestmentService.perform(request));

        verifyNoInteractions(investmentRepository);                                    /// 3. Confirms this did not happen
    }

    @Test
    void perform_whenInsufficientBalance_throwsException(){

        //rule: when a method is void ie has no return type we use do throw for example ValidateBalanceForInvestment is a void method

        when(validations.getUserInfo(UUID)).thenReturn(user);
        when(validations.findSubWalletIfExists("MW-1","SW-1"))
                .thenReturn(subWallet);
/// 1.
        doThrow(new RuntimeException("Insufficient Balance"))                           // throw exception
                .when(investmentValidation)                                             // on this object
                .ValidateBalanceForInvestment(anyDouble());                             // whenever this method is called with any double value

/// 2.
        assertThrows(RuntimeException.class,
                () -> createInvestmentService.perform(request));

/// 3.
        verify(investmentRepository, never()).save(any());

    }

    @Test
    void perform_validateSubWalletForInvestment_throwsException(){

       when(validations.getUserInfo(UUID)).thenReturn(user);
       when(validations.findSubWalletIfExists("MW-1", "SW-1"))
               .thenReturn(subWallet);

/// 1.
       doThrow(new RuntimeException("Sub-Wallet already exist"))
               .when(validations)
               .validateSubWalletForInvestment(subWallet);

/// 2.
        assertThrows(RuntimeException.class,
                () -> createInvestmentService.perform(request));

/// 3.
        verify(investmentRepository, never()).save(any());

    }

    @Test
    void perform_PortfolioModelNotFound_throwsException(){

        when(validations.getUserInfo(UUID)).thenReturn(user);
        when(validations.findSubWalletIfExists("MW-1", "SW-1"))
                .thenReturn(subWallet);

/// 1.
        when(investmentValidation.validatePortfolioModelByName("MEDIUM"))
                .thenThrow(new RuntimeException("Portfolio Model name not found"));

/// 2.
        assertThrows(RuntimeException.class,
                ()-> createInvestmentService.perform(request));

///3.
        verify(investmentRepository, never()).save(any());
    }

    @Test
    void perform_UnitsNotFound_throwsException(){

        when(validations.getUserInfo(UUID)).thenReturn(user);
        when(validations.findSubWalletIfExists("MW-1","SW-1"))
                .thenReturn(subWallet);

        when(investmentValidation.validatePortfolioModelByName("MEDIUM"))
                .thenReturn(portfolioModel);

/// 1.
        when(investmentValidation.findUnitByDate(any(),any()))
                .thenThrow(new RuntimeException("Unit not found"));

///  2.
        assertThrows(RuntimeException.class,
                () -> createInvestmentService.perform(request));

///  3.
        verify(investmentRepository, never()).save(any());
    }

/** Positive test case (MAIN TEST) */

    @Test
    void perform_success_createInvestmentAndTransaction(){

        when(validations.getUserInfo(UUID)).thenReturn(user);
        when(validations.findSubWalletIfExists("MW-1","SW-1"))
                .thenReturn(subWallet);

        when(request.getRiskLevel())
                .thenReturn(RiskLevel.MEDIUM);


        when(investmentValidation.validatePortfolioModelByName("MEDIUM"))
                .thenReturn(portfolioModel);

        when(investmentValidation.getUnitsForDate(any(),any()))
                .thenReturn(units);

///  1.
        InvestmentResponse investmentResponse = createInvestmentService.perform(request);

///  2.
        assertNotNull(investmentResponse);
        assertTrue(investmentResponse.getMessage().contains("investment"));

///  3.
        verify(investmentRepository, times(1)).save(any(Investment.class));
        verify(subWalletRepository, times(1)).save(subWallet);
        verify(transactionRepository, times(1)).save(any(Transaction.class));

    }
    @Test
    void getInvestmentRequestType_shouldReturnCreateInvestment() {

        InvestmentRequestType type =
                createInvestmentService.getInvestmentRequestType();

        assertEquals(InvestmentRequestType.CREATE_INVESTMENT, type);
    }

}

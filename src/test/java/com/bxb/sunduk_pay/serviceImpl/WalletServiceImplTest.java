package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.exception.InvestmentException;
import com.bxb.sunduk_pay.factories.WalletFactory.WalletOperation;
import com.bxb.sunduk_pay.factories.WalletFactory.WalletOperationFactory;
import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import com.bxb.sunduk_pay.postgress.model.Units;
import com.bxb.sunduk_pay.repository.InvestmentRepository;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.repository.MasterWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.InvestmentUtil;
import com.bxb.sunduk_pay.util.PaymentMethod;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.util.TransactionType;
import com.bxb.sunduk_pay.validations.InvestmentValidation;
import com.bxb.sunduk_pay.validations.Validations;
import com.google.api.client.util.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    private MasterWalletRepository masterWalletRepository;

    @Mock
    private MainWalletRepository mainWalletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @Mock
    private WalletOperationFactory walletOperationFactory;

    @Mock
    private Validations validations;

    @Mock
    private InvestmentValidation investmentValidation;

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private InvestmentUtil investmentUtil;

    @InjectMocks
    private WalletServiceImpl walletService;


    private User user;
    private MasterWallet masterWallet;
    private MainWallet mainWallet;
    private SubWallet subWallet;

    @BeforeEach
    void setUp() {

        user = User.builder()
                .uuid("user-123")
                .transactionHistory(new ArrayList<>())
                .build();

        masterWallet = MasterWallet.builder()
                .masterWalletId("mw-1")
                .balance(1000.0)
                .build();

        mainWallet = MainWallet.builder()
                .mainWalletId("main-1")
                .balance(500.0)
                .subWallets(new ArrayList<>())
                .build();

        subWallet = SubWallet.builder()
                .subWalletId("sub-1")
                .subWalletName("Food")
                .balance(300.0)
                .isInvested(false)
                .build();
    }

    @Test
    void payMoneyFromSubWallet() {

        MainWalletRequest request = MainWalletRequest.builder()
                .uuid("user-123")
                .amount(100.0)
                .sourceWalletId("sub-1")
                .requestType(RequestType.UPDATE)
                .paymentMethod(PaymentMethod.CARD)
                .transactionType(TransactionType.DEBIT)
                .build();


        when(validations.getUserInfo("user-123")).thenReturn(user);
        when(validations.getMasterWalletInfo("user-123")).thenReturn(masterWallet);
        when(validations.getMainWalletInfo("user-123")).thenReturn(mainWallet);
        when(validations.findSubWalletIfExists("main-1", "sub-1"))
                .thenReturn(subWallet);


        when(transactionMapper.toTransactionEvent(any()))
                .thenReturn(new TransactionEvent());


        MainWalletResponse response = walletService.payMoney(request);


        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(200.0, subWallet.getBalance());
        assertEquals(900.0, masterWallet.getBalance());


        verify(transactionRepository).saveAll(any());
        verify(masterWalletRepository).save(masterWallet);
        verify(mainWalletRepository).save(mainWallet);
        verify(kafkaTemplate, atLeastOnce()).send(anyString(), any());
    }

    @Test
    void payMoneyFromInvestedSubWallet() {

        subWallet.setIsInvested(true);

        Investment investment = Investment.builder()
                .investmentId("inv-1")
                .isActive(true)
                .portfolioModelId(1L)
                .UnitPurchaseDate(LocalDateTime.now().minusDays(1))
                .build();

        PortfolioModel portfolioModel = new PortfolioModel();
        Units unit = new Units();

        MainWalletRequest request = MainWalletRequest.builder()
                .uuid("user-123")
                .amount(100.0)
                .sourceWalletId("sub-1")
                .requestType(RequestType.UPDATE)
                .paymentMethod(PaymentMethod.CARD)
                .transactionType(TransactionType.DEBIT)
                .build();

        when(validations.getUserInfo("user-123")).thenReturn(user);
        when(validations.getMasterWalletInfo("user-123")).thenReturn(masterWallet);
        when(validations.getMainWalletInfo("user-123")).thenReturn(mainWallet);
        when(validations.findSubWalletIfExists("main-1", "sub-1"))
                .thenReturn(subWallet);

        when(investmentValidation.getInvestmentBySubWalletId("sub-1"))
                .thenReturn(investment);
        when(investmentValidation.getPortfolioModelById(1L))
                .thenReturn(portfolioModel);
        when(investmentValidation.findNextUnit(any(), any()))
                .thenReturn(unit);
        when(investmentUtil.updateInvestmentOnDebit(any(), any(), anyDouble()))
                .thenReturn(investment);

        when(transactionMapper.toTransactionEvent(any()))
                .thenReturn(new TransactionEvent());

        MainWalletResponse response = walletService.payMoney(request);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(200.0, subWallet.getBalance());
        assertEquals(900.0, masterWallet.getBalance());

        verify(investmentRepository).save(investment);
        verify(kafkaTemplate).send(eq("transaction-topic"), any());
    }


    @Test
    void payMoneyFromInactiveInvestment(){

        subWallet.setIsInvested(true);

        Investment inactiveInvestment = Investment.builder()
                .isActive(false)
                .build();

        MainWalletRequest request = MainWalletRequest.builder()
                .uuid("user-123")
                .amount(100.0)
                .sourceWalletId("sub-1")
                .requestType(RequestType.UPDATE)
                .transactionType(TransactionType.DEBIT)
                .build();

        when(validations.getUserInfo("user-123")).thenReturn(user);
        when(validations.getMasterWalletInfo("user-123")).thenReturn(masterWallet);
        when(validations.getMainWalletInfo("user-123")).thenReturn(mainWallet);
        when(validations.findSubWalletIfExists("main-1", "sub-1"))
                .thenReturn(subWallet);

        when(investmentValidation.getInvestmentBySubWalletId("sub-1"))
                .thenReturn(inactiveInvestment);

        assertThrows(InvestmentException.class,
                () -> walletService.payMoney(request));

        verify(investmentRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(any(), any());
    }



    @Test
    void payMoneyFromMainWallet() {

        MainWalletRequest request = MainWalletRequest.builder()
                .uuid("user-123")
                .amount(100.0)
                .sourceWalletId("main-1")
                .requestType(RequestType.UPDATE)
                .paymentMethod(PaymentMethod.CARD)
                .transactionType(TransactionType.DEBIT)
                .build();


        when(validations.getUserInfo("user-123")).thenReturn(user);
        when(validations.getMasterWalletInfo("user-123")).thenReturn(masterWallet);
        when(validations.getMainWalletInfo("user-123")).thenReturn(mainWallet);

        when(transactionMapper.toTransactionEvent(any()))
                .thenReturn(new TransactionEvent());


        MainWalletResponse response = walletService.payMoney(request);


        assertNotNull(response);
        assertEquals("SUCCESS", response.getStatus());
        assertEquals(400.0, mainWallet.getBalance());
        assertEquals(900.0, masterWallet.getBalance());


        verify(transactionRepository).saveAll(any());
        verify(masterWalletRepository).save(masterWallet);
        verify(mainWalletRepository).save(mainWallet);
        verify(kafkaTemplate, atLeastOnce()).send(anyString(), any());
    }

    @Test
    void payMoneyInsufficientBalanceException() {

        MainWalletRequest request = MainWalletRequest.builder()
                .uuid("user-123")
                .requestType(RequestType.UPDATE)
                .amount(1000.0)
                .build();

        when(validations.getUserInfo("user-123")).thenReturn(user);
        when(validations.getMasterWalletInfo("user-123")).thenReturn(masterWallet);
        when(validations.getMainWalletInfo("user-123")).thenReturn(mainWallet);

        doThrow(RuntimeException.class)
                .when(validations)
                .validateBalance(anyDouble(), anyDouble());

        assertThrows(RuntimeException.class,
                () -> walletService.payMoney(request));
    }


    @Test
    void addMoneyToMainWallet() {
        String uuid = "user-123";

        MainWalletRequest request = MainWalletRequest.builder()
                .uuid(uuid)
                .amount(200.0)
                .requestType(RequestType.UPDATE)
                .build();

        when(validations.getUserInfo(uuid)).thenReturn(user);
        when(validations.getMasterWalletInfo(uuid)).thenReturn(masterWallet);
        when(validations.getMainWalletInfo(uuid)).thenReturn(mainWallet);
        when(validations.findSubWalletIfExists(any(), any()))
                .thenReturn(null);

        when(transactionMapper.toTransactionEvent(any()))
                .thenReturn(new TransactionEvent());

        MainWalletResponse response =
                walletService.addMoney(request);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(1200.0, masterWallet.getBalance());
        assertEquals(700.0, mainWallet.getBalance());

        verify(transactionRepository).saveAll(anyList());
        verify(kafkaTemplate).send(eq("transaction-topic"), any());
    }

    @Test
    void addMoneyToInvestedSubWallet() {

        subWallet.setIsInvested(true);

        Investment investment = Investment.builder()
                .investmentId("inv-1")
                .isActive(true)
                .portfolioModelId(1L)
                .UnitPurchaseDate(LocalDateTime.now().minusDays(1))
                .build();

        PortfolioModel portfolioModel = new PortfolioModel();
        Units unit = new Units();

        MainWalletRequest request = MainWalletRequest.builder()
                .uuid("user-123")
                .amount(200.0)
                .requestType(RequestType.UPDATE)
                .targetWalletId("sub-1")
                .build();

        when(validations.getUserInfo("user-123")).thenReturn(user);
        when(validations.getMasterWalletInfo("user-123")).thenReturn(masterWallet);
        when(validations.getMainWalletInfo("user-123")).thenReturn(mainWallet);
        when(validations.findSubWalletIfExists("main-1", "sub-1"))
                .thenReturn(subWallet);

        when(investmentValidation.getInvestmentBySubWalletId("sub-1"))
                .thenReturn(investment);
        when(investmentValidation.getPortfolioModelById(1L))
                .thenReturn(portfolioModel);
        when(investmentValidation.findNextUnit(any(), any()))
                .thenReturn(unit);
        when(investmentUtil.updateInvestmentOnCredit(any(), any(), anyDouble()))
                .thenReturn(investment);

        when(transactionMapper.toTransactionEvent(any()))
                .thenReturn(new TransactionEvent());

        MainWalletResponse response = walletService.addMoney(request);

        assertEquals("SUCCESS", response.getStatus());
        assertEquals(500.0, subWallet.getBalance());
        assertEquals(1200.0, masterWallet.getBalance());

        verify(investmentRepository).save(investment);
    }

    @Test
    void addMoneyInvestmentIsInactiveInvestmentException() {

        subWallet.setIsInvested(true);

        Investment investment = Investment.builder()
                .investmentId("inv-1")
                .isActive(false)
                .portfolioModelId(1L)
                .UnitPurchaseDate(LocalDateTime.now().minusDays(1))
                .build();

        MainWalletRequest request = MainWalletRequest.builder()
                .uuid("user-123")
                .amount(100.0)
                .sourceWalletId("sub-1")
                .requestType(RequestType.UPDATE)
                .paymentMethod(PaymentMethod.CARD)
                .transactionType(TransactionType.CREDIT)
                .build();

        when(validations.getUserInfo("user-123")).thenReturn(user);
        when(validations.getMasterWalletInfo("user-123")).thenReturn(masterWallet);
        when(validations.getMainWalletInfo("user-123")).thenReturn(mainWallet);
        when(validations.findSubWalletIfExists("main-1", null))
                .thenReturn(subWallet);

        when(investmentValidation.getInvestmentBySubWalletId("sub-1"))
                .thenReturn(investment);

        InvestmentException exception = assertThrows(
                InvestmentException.class,
                () -> walletService.addMoney(request)
        );

        assertEquals(
                "Cannot process payment from an inactive investment.",
                exception.getMessage()
        );

    }


    @Test
    void walletCrud() {

        MainWalletRequest request = MainWalletRequest.builder()
                .uuid("user-123")
                .requestType(RequestType.CREATE)
                .build();
        WalletOperation operation = mock(WalletOperation.class);
        MainWalletResponse expected =
                MainWalletResponse.builder().status("SUCCESS").build();

        when(walletOperationFactory.getWalletService(RequestType.CREATE))
                .thenReturn(operation);

        when(operation.perform(request)).thenReturn(expected);

        MainWalletResponse response =
                walletService.walletCrud(request);

        assertEquals(expected, response);
        verify(operation).perform(request);
    }

    @Test
    void testAddDummy(){
        MainWalletRequest request = MainWalletRequest.builder()
                .uuid("user-123")
                .amount(50.0)
                .requestType(RequestType.CREATE)
                .transactionType(TransactionType.DEBIT)
                .paymentMethod(PaymentMethod.UPI)
                .build();

        when(validations.getUserInfo("user-123"))
                .thenReturn(user);
        when(validations.getMainWalletInfo("user-123"))
                .thenReturn(mainWallet);

        walletService.addDummy(request);

        verify(transactionRepository).save(any(Transaction.class));
    }
}
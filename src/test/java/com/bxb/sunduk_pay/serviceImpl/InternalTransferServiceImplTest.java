package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.encryption.MpinValidations;
import com.bxb.sunduk_pay.exception.InvestmentException;
import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.model.Investment;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.InvestmentRepository;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.InvestmentUtil;
import com.bxb.sunduk_pay.validations.InvestmentValidation;
import com.bxb.sunduk_pay.validations.Validations;
import com.bxb.sunduk_pay.wrapper.WalletWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InternalTransferServiceImplTest {

    @InjectMocks
    private InternalTransferServiceImpl internalTransferService;

    @Mock
    private MpinValidations mpinValidations;

    @Mock
    private Validations validations;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private MainWalletRepository mainWalletRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private InvestmentValidation investmentValidation;

    @Mock
    private InvestmentUtil investmentUtil;

    private User user;
    private MainWallet mainWallet;
    private WalletWrapper sourceWallet;
    private WalletWrapper targetWallet;

    /** This will run before each test case */

    @BeforeEach
    void setup() {
        user = new User();
        user.setUuid("User-1");
        user.setTransactionHistory(new ArrayList<>());

        mainWallet = new MainWallet();
        mainWallet.setMainWalletId("MW1");
        mainWallet.setBalance(1500.0);

        SubWallet sourceSubWallet = new SubWallet();
        sourceSubWallet.setSubWalletId("SW-1");
        sourceSubWallet.setSubWalletName("Source Wallet");
        sourceSubWallet.setBalance(1000d);
        sourceSubWallet.setIsInvested(false);

        SubWallet targetSubWallet = new SubWallet();
        targetSubWallet.setSubWalletId("TW-1");
        targetSubWallet.setSubWalletName("Target Wallet");
        targetSubWallet.setBalance(500d);
        targetSubWallet.setIsInvested(false);

        sourceWallet = new WalletWrapper(sourceSubWallet);
        targetWallet = new WalletWrapper(targetSubWallet);


        //Positive case (If the code runs fine)
}


     @Test
     void successfulTransfer_save_andPublishKafka(){
///1
        doNothing().when(validations)
                .validateBalance(1000.0,200.0);

        when(transactionMapper.toTransactionEvent(any()))
                .thenReturn(new TransactionEvent());

///2
         MainWalletResponse response = internalTransferService.doInternalTransfer(
                 user,
                 mainWallet,
                 200d,
                 sourceWallet,
                 targetWallet,
                 1000d,
                 500d,
                 "1234"
         );

///3
         assertEquals("SUCCESS", response.getStatus());




        assertEquals("SUCCESS", response.getStatus());
        assertEquals(800.0, response.getNewSourceWalletBalance());
        assertEquals(700.0, response.getNewTargetWalletBalance());

        verify(transactionRepository).saveAll(anyList());
        verify(mainWalletRepository).save(mainWallet);
        verify(kafkaTemplate)
                .send(eq("transaction-topic"), any(TransactionEvent.class));
    }

    // ================= VALIDATION FAILURES (COMBINED) =================
    @ParameterizedTest
    @MethodSource("validationFailures")
    void shouldFailForAllValidationErrors(
            Double amount,
            RuntimeException exception) {

///1
        doThrow(exception)
                .when(validations)
                .validateBalance(anyDouble(), eq(amount));

///2
        assertThrows(exception.getClass(), () ->
                internalTransferService.doInternalTransfer(
                        user,
                        mainWallet,
                        amount,
                        sourceWallet,
                        targetWallet,
                        1000.0,
                        500.0,
                        "1234"
                )
        );

///3
        verifyNoInteractions(kafkaTemplate);
    }

    static Stream<Arguments> validationFailures() {
        return Stream.of(
                Arguments.of(2000.0,
                        new RuntimeException("Insufficient balance")),
                Arguments.of(-10.0,
                        new IllegalArgumentException("Invalid amount"))
        );
    }

    // ================= BUSINESS RULE FAILURE =================
    @Test
    void shouldFailWhenSourceInvestmentInactive() {

/// 1
        SubWallet investedSubWallet = new SubWallet();
        investedSubWallet.setSubWalletId("SW1");
        investedSubWallet.setSubWalletName("Source Wallet");
        investedSubWallet.setBalance(1000.0);
        investedSubWallet.setIsInvested(true);

        sourceWallet = new WalletWrapper(investedSubWallet);

        Investment investment = new Investment();
        investment.setActive(false);

        when(investmentValidation
                .getInvestmentBySubWalletId("SW1"))
                .thenReturn(investment);

        doNothing().when(validations)
                .validateBalance(anyDouble(), anyDouble());

/// 2
        assertThrows(InvestmentException.class, () ->
                internalTransferService.doInternalTransfer(
                        user,
                        mainWallet,
                        100.0,
                        sourceWallet,
                        targetWallet,
                        1000.0,
                        500.0,
                        "1234"
                )
        );

///3
        verifyNoInteractions(kafkaTemplate);
    }

    // ================= INFRA FAILURE : DB =================
    @Test
    void shouldFailWhenTransactionSaveFails() {

///1
        doNothing().when(validations)
                .validateBalance(anyDouble(), anyDouble());

        when(transactionRepository.saveAll(anyList()))
                .thenThrow(new RuntimeException("DB down"));

///2
        assertThrows(RuntimeException.class, () ->
                internalTransferService.doInternalTransfer(
                        user,
                        mainWallet,
                        100.0,
                        sourceWallet,
                        targetWallet,
                        1000.0,
                        500.0,
                        "1234"
                )
        );

///3
        verifyNoInteractions(kafkaTemplate);
    }

    // ================= INFRA FAILURE : KAFKA =================
    @Test
    void shouldFailWhenKafkaPublishFails() {

///1
        doNothing().when(validations)
                .validateBalance(anyDouble(), anyDouble());

        when(transactionMapper.toTransactionEvent(any()))
                .thenReturn(new TransactionEvent());

        doThrow(new RuntimeException("Kafka down"))
                .when(kafkaTemplate)
                .send(anyString(), any());

///2
        assertThrows(RuntimeException.class, () ->
                internalTransferService.doInternalTransfer(
                        user,
                        mainWallet,
                        100.0,
                        sourceWallet,
                        targetWallet,
                        1000.0,
                        500.0,
                        "1234"
                )
        );

///3
        verify(transactionRepository).saveAll(anyList());
    }
}


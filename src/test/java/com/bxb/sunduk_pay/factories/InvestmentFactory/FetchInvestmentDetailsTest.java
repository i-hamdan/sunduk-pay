package com.bxb.sunduk_pay.factories.InvestmentFactory;

import com.bxb.sunduk_pay.Mappers.InvestmentMapper;
import com.bxb.sunduk_pay.exception.InvalidPayloadException;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.InvestmentDailyHistoryRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.InvestmentRequest;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import com.bxb.sunduk_pay.util.InvestmentGraphData;
import com.bxb.sunduk_pay.util.InvestmentRequestType;
import com.bxb.sunduk_pay.util.InvestmentsFetchType;
import com.bxb.sunduk_pay.validations.InvestmentValidation;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FetchInvestmentDetailsTest {

    @Mock
    private InvestmentValidation stockValidation;

    @Mock
    private Validations validations;

    @Mock
    private InvestmentMapper investmentMapper;

    @Mock
    private InvestmentGraphData investmentGraphData;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private InvestmentDailyHistoryRepository investmentHistoryRepository;

    @InjectMocks
    private FetchInvestmentDetails fetchInvestmentDetails;

//    private String uuid;
//    private String mainWalletId;
//    private String subWalletId;
//    private String investmentId;
//
//    private User user;
//    private MainWallet mainWallet;
//    private SubWallet subWallet;
//    private Investment investment;

//    @BeforeEach
//    void setup() {
//        uuid = UUID.randomUUID().toString();
//        mainWalletId = UUID.randomUUID().toString();
//        subWalletId = UUID.randomUUID().toString();
//        investmentId = UUID.randomUUID().toString();
//
//        mainWallet = new MainWallet();
//        mainWallet.setMainWalletId(mainWalletId);
//
//        subWallet = new SubWallet();
//        subWallet.setSubWalletId(subWalletId);
//
//        user = new User();
//        user.setUuid(uuid);
//        user.setMainWallet(mainWallet);
//
//        investment = new Investment();
//        investment.setInvestmentId(investmentId);
//        investment.setInvestmentAmount(1000.0);
//        investment.setCurrentValue(1500.0);
//    }

    @Test
    void testGetInvestmentRequestType() {
        InvestmentRequestType type = fetchInvestmentDetails.getInvestmentRequestType();
        assertEquals(InvestmentRequestType.FETCH_INVESTMENTS, type,
                "Should return investment request type FETCH_INVESTMENTS");
    }

    @Test
    void testFetchPotInvestmentsSuccessfully() {


        InvestmentRequest request = InvestmentRequest.builder()
                .uuid("user-123")
                .subWalletId("sub-1")
                .investmentsFetchType(InvestmentsFetchType.POT_INVESTMENTS)
                .build();

        User user = new User();
        user.setUuid("user-123");

        MainWallet mainWallet = new MainWallet();
        mainWallet.setMainWalletId("main-1");

        SubWallet subWallet = new SubWallet();
        subWallet.setSubWalletId("sub-1");

        Investment investment = new Investment();
        investment.setInvestmentId("inv-1");

        InvestmentResponse mappedResponse = InvestmentResponse.builder()
                .message("Pot investment fetched successfully.")
                .build();

        when(validations.getUserInfo("user-123")).thenReturn(user);
        when(validations.getMainWalletInfo("user-123")).thenReturn(mainWallet);
        when(validations.findSubWalletIfExists("main-1", "sub-1"))
                .thenReturn(subWallet);
        when(stockValidation.getInvestmentBySubWalletId("sub-1"))
                .thenReturn(investment);

        when(transactionRepository
                .findAllByUserUuidAndWalletId(anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        when(investmentGraphData.withdrawalTrendsGraphData(anyList()))
                .thenReturn(Collections.emptyMap());

        when(investmentHistoryRepository
                .findByInvestmentInvestmentId("inv-1"))
                .thenReturn(Collections.emptyList());

        when(investmentGraphData.dailyInvestmentGraphData(anyList()))
                .thenReturn(Collections.emptyMap());

        when(investmentMapper.toInvestmentResponse(any(), any(), any()))
                .thenReturn(mappedResponse);


        InvestmentResponse response = fetchInvestmentDetails.perform(request);

        assertNotNull(response);
        assertEquals("Pot investment fetched successfully.", response.getMessage());
    }

    @Test
    void testFetchAllInvestmentSuccessfully() {

        InvestmentRequest request = InvestmentRequest.builder()
                .uuid("user-123")
                .subWalletId("sub-1")
                .investmentsFetchType(InvestmentsFetchType.ALL_INVESTMENTS)
                .build();

        User user = new User();
        user.setUuid("user-123");

        Investment investment = new Investment();
        investment.setInvestmentAmount(1000.0);
        investment.setCurrentValue(1200.0);

        InvestmentDailyHistory history = new InvestmentDailyHistory();

        history.setSnapshotDate(LocalDate.now().minusMonths(1));
        history.setUnits(10.0);
        history.setUnitPrice(100.0);

        when(validations.getUserInfo("user-123")).thenReturn(user);
        when(stockValidation.getInvestmentsByUserUuid("user-123"))
                .thenReturn(List.of(investment));
        when(investmentHistoryRepository
                .findByUserUuidAndSnapshotDateBetween(
                        anyString(),
                        any(LocalDate.class),
                        any(LocalDate.class)
                ))
                .thenReturn(List.of(history));
        when(investmentGraphData.dailyCombinedInvestmentGraphData(anyMap()))
                .thenReturn(Collections.emptyMap());

        InvestmentResponse response =
                fetchInvestmentDetails.perform(request);

        assertNotNull(response);
        assertEquals(1000.0, response.getTotalInvestedAmount());
        assertEquals("1,200.00", response.getTotalCurrentValue());

    }

    @Test
    void testReturnZeroValuesWhenNoInvestments() {

        InvestmentRequest request = InvestmentRequest.builder()
                .uuid("user-123").investmentsFetchType(InvestmentsFetchType.ALL_INVESTMENTS)
                .build();

        User user = new User();
        user.setUuid("user-123");

        when(validations.getUserInfo("user-123")).thenReturn(user);
        when(stockValidation.getInvestmentsByUserUuid("user-123"))
                .thenReturn(Collections.emptyList());

        InvestmentResponse response =
                fetchInvestmentDetails.perform(request);

        assertEquals(0.0, response.getTotalInvestedAmount());
        assertEquals("0.00", response.getTotalCurrentValue());
        assertEquals(0.0, response.getGain1MonthPercent());
    }

    @Test
    void testInvalidFetchTypeException() {

        InvestmentRequest request = InvestmentRequest.builder()
                .uuid("user-123")
                .investmentsFetchType(null)
                .build();

        when(validations.getUserInfo("user-123"))
                .thenReturn(new User());

        assertThrows(
                InvalidPayloadException.class,
                () -> fetchInvestmentDetails.perform(request)
        );
    }


    @Test
    void getInvestmentRequestTypeReturnFetchInvestments() {
        assertEquals(
                InvestmentRequestType.FETCH_INVESTMENTS,
                fetchInvestmentDetails.getInvestmentRequestType()
        );


    }

    @Test
    void testHistoryIsEmpty() {

        InvestmentRequest request = InvestmentRequest.builder()
                .uuid("user-123").investmentsFetchType(InvestmentsFetchType.ALL_INVESTMENTS)
                .build();

        User user = User.builder().uuid("user-123")
                .build();

        Investment inv1 = Investment.builder()
                .investmentAmount(1000.0).currentValue(1200.0)
                .build();

        Investment inv2 = Investment.builder()
                .investmentAmount(2000.0).currentValue(2500.0)
                .build();

        when(validations.getUserInfo("user-123")).thenReturn(user);
        when(stockValidation.getInvestmentsByUserUuid("user-123"))
                .thenReturn(List.of(inv1, inv2));
        when(investmentHistoryRepository
                .findByUserUuidAndSnapshotDateBetween(
                        eq("user-123"),
                        any(LocalDate.class),
                        any(LocalDate.class)))
                .thenReturn(List.of());

        InvestmentResponse response = fetchInvestmentDetails.perform(request);

        assertNotNull(response);

        assertEquals(3000.0, response.getTotalInvestedAmount());
        assertEquals("3,700.00", response.getTotalCurrentValue());
        assertEquals(700.0, response.getTotalNetProfitLoss());
        assertEquals(0.0, response.getGain1MonthPercent());
        assertEquals(0.0, response.getGain6MonthsPercent());
    }


    @Test
    void testIfHistoryIsNull() {

        InvestmentRequest request = InvestmentRequest.builder()
                .uuid("user-123").investmentsFetchType(InvestmentsFetchType.ALL_INVESTMENTS)
                .build();

        User user = User.builder().uuid("user-123")
                .build();

        Investment investment = Investment.builder()
                .investmentAmount(1500.0).currentValue(1800.0)
                .build();

        when(validations.getUserInfo("user-123")).thenReturn(user);
        when(stockValidation.getInvestmentsByUserUuid("user-123"))
                .thenReturn(List.of(investment));
        when(investmentHistoryRepository
                .findByUserUuidAndSnapshotDateBetween(
                        eq("user-123"),
                        any(LocalDate.class),
                        any(LocalDate.class)))
                .thenReturn(null);

        InvestmentResponse response = fetchInvestmentDetails.perform(request);

        assertNotNull(response);

        assertEquals(1500.0, response.getTotalInvestedAmount());
        assertEquals("1,800.00", response.getTotalCurrentValue());
        assertEquals(300.0, response.getTotalNetProfitLoss());
        assertEquals(0.0, response.getGain1MonthPercent());
        assertEquals(0.0, response.getGain6MonthsPercent());
    }

}
package com.bxb.sunduk_pay.factories.InvestmentFactory;

import com.bxb.sunduk_pay.exception.InvestmentException;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.postgress.model.PortfolioModel;
import com.bxb.sunduk_pay.postgress.model.Units;
import com.bxb.sunduk_pay.repository.InvestmentDailyHistoryRepository;
import com.bxb.sunduk_pay.repository.InvestmentRepository;
import com.bxb.sunduk_pay.repository.SubWalletRepository;
import com.bxb.sunduk_pay.request.InvestmentRequest;
import com.bxb.sunduk_pay.response.InvestmentResponse;
import com.bxb.sunduk_pay.util.InvestmentRequestType;
import com.bxb.sunduk_pay.util.RiskLevel;
import com.bxb.sunduk_pay.validations.InvestmentValidation;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateRiskLevelServiceTest {

    @Mock
    private Validations validations;

    @Mock
    private InvestmentValidation investmentValidation;

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private InvestmentDailyHistoryRepository dailyHistoryRepository;

    @Mock
    private SubWalletRepository subWalletRepository;

    @InjectMocks
    private UpdateRiskLevelService service;

    private User user;
    private MainWallet mainWallet;
    private SubWallet subWallet;
    private Investment investment;
    private InvestmentDailyHistory history;
    private PortfolioModel portfolio;
    private Units units;
    private RiskLevel riskLevel;


    //Positive testing of methods.

    @Test
    void testGetInvestmentCancellationRequest(){
        InvestmentRequestType type = service.getInvestmentRequestType();

        assertEquals(InvestmentRequestType.CHANGE_RISK_LEVEL,type,
                "Updates the risk level of INVESTMENT.");
    }


    String uuid = UUID.randomUUID().toString();
    String subWalletId = UUID.randomUUID().toString();
    String mainWalletId = UUID.randomUUID().toString();

    @BeforeEach
    void setup() {

        mainWallet = new MainWallet();
        mainWallet.setMainWalletId(mainWalletId);

        user = new User();
        user.setUuid(uuid);
        user.setMainWallet(mainWallet);

        subWallet = new SubWallet();
        subWallet.setSubWalletId(subWalletId);
        subWallet.setIsInvested(true);
        subWallet.setRiskLevel(RiskLevel.LOW);

        investment = new Investment();
        investment.setSubWallet(subWallet);
        investment.setActive(true);
        investment.setRiskLevel(RiskLevel.LOW);
        investment.setCurrentValue(1000.0);

        history = new InvestmentDailyHistory();
        history.setSnapshotDate(LocalDate.now());

        portfolio = new PortfolioModel();
        portfolio.setId(1L);

        units = Units.builder()
                .combinedValue(BigDecimal.valueOf(100))
                .build();
    }

    @Test
    void testRiskLevelUpdate(){
        InvestmentRequest request = InvestmentRequest.builder()
                .uuid(uuid).subWalletId(subWalletId).riskLevel(RiskLevel.HIGH)
                .requestType(InvestmentRequestType.CHANGE_RISK_LEVEL).build();

        when(validations.getUserInfo(uuid)).thenReturn(user);
        when(validations.findSubWalletIfExists(mainWalletId, subWalletId)).thenReturn(subWallet);
        when(investmentValidation.getInvestmentBySubWalletId(request.getSubWalletId()))
                .thenReturn(investment);
        when(investmentValidation.validateRiskLevel("LOW","HIGH"))
                .thenReturn(RiskLevel.HIGH);
        when(investmentValidation.getPortfolioModelByRiskLevel("HIGH"))
                .thenReturn(portfolio);
        when(dailyHistoryRepository.findTopByInvestmentOrderBySnapshotDateDesc(investment))
                .thenReturn(history);
        when(investmentValidation.getUnitsForDate(portfolio, history.getSnapshotDate()))
                .thenReturn(units);

        InvestmentResponse response = service.perform(request);

        assertNotNull(response);
        assertEquals("Risk level updated successfully to HIGH", response.getMessage());

        verify(investmentRepository).save(investment);
        verify(subWalletRepository).save(subWallet);
    }

// Negative testing of method (Handling Exception)

    @Test
    void testInvestmentIsInActiveException(){

        investment.setActive(false);
        InvestmentRequest request = InvestmentRequest.builder().uuid(uuid).subWalletId(subWalletId)
                .riskLevel(RiskLevel.MEDIUM).build();

        when(validations.getUserInfo(uuid)).thenReturn(user);
        when(validations.findSubWalletIfExists(mainWalletId, subWalletId)).thenReturn(subWallet);
        when(investmentValidation.getInvestmentBySubWalletId(request.getSubWalletId()))
                .thenReturn(investment);

        InvestmentException ex = assertThrows(InvestmentException.class,
                ()-> service.perform(request));

        assertEquals("Cannot change risk level for inactive investment.", ex.getMessage());
    }

    @Test
    void testSubwalletIsNotInvestedException(){

        subWallet.setIsInvested(false);
        InvestmentRequest request = InvestmentRequest.builder().uuid(uuid).subWalletId(subWalletId)
                .riskLevel(RiskLevel.MEDIUM).build();

        when(validations.getUserInfo(uuid)).thenReturn(user);
        when(validations.findSubWalletIfExists(mainWalletId, subWalletId)).thenReturn(subWallet);
        when(investmentValidation.getInvestmentBySubWalletId(request.getSubWalletId()))
                .thenReturn(investment);

        InvestmentException ex = assertThrows(InvestmentException.class,
                ()-> service.perform(request));

        assertEquals("Cannot change risk level for inactive investment.", ex.getMessage());
    }

    @Test
    void lastSnapshotNotNullException(){

        InvestmentRequest request = InvestmentRequest.builder()
                .uuid(uuid).subWalletId(subWalletId).riskLevel(RiskLevel.HIGH)
                .requestType(InvestmentRequestType.CHANGE_RISK_LEVEL).build();

        when(validations.getUserInfo(uuid)).thenReturn(user);
        when(validations.findSubWalletIfExists(mainWalletId, subWalletId)).thenReturn(subWallet);
        when(investmentValidation.getInvestmentBySubWalletId(request.getSubWalletId()))
                .thenReturn(investment);
        when(investmentValidation.validateRiskLevel("LOW","HIGH"))
                .thenReturn(RiskLevel.HIGH);
        when(investmentValidation.getPortfolioModelByRiskLevel("HIGH"))
                .thenReturn(portfolio);
        when(dailyHistoryRepository.findTopByInvestmentOrderBySnapshotDateDesc(investment))
                .thenReturn(null);

        InvestmentException ex = assertThrows(InvestmentException.class,
                ()-> service.perform(request));

        assertEquals("Cannot change risk level before first P/L snapshot", ex.getMessage());
    }


}
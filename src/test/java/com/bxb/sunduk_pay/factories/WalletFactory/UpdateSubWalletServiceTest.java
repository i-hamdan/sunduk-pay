package com.bxb.sunduk_pay.factories.WalletFactory;

import com.bxb.sunduk_pay.exception.CannotUpdateWalletException;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.util.UpdateWalletActionType;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UpdateSubWalletServiceTest {

    @Mock
    private MainWalletRepository mainWalletRepository;
    @Mock
    private Validations validations;
    @InjectMocks
    private UpdateSubWalletService updateSubWalletService;

    @Test
    public void runUpdateTargetBalanceTest() {
        MainWalletRequest request = MainWalletRequest.builder().uuid(UUID.randomUUID().toString())
                .mainWalletId(UUID.randomUUID().toString())
                .subWalletId(UUID.randomUUID().toString())
                .actionType(UpdateWalletActionType.GOAL_AMOUNT)
                .targetBalance(2000d)
                .requestType(RequestType.UPDATE).build();

        User user = User.builder().uuid(UUID.randomUUID().toString()).build();
        MainWallet mainWallet =  MainWallet.builder().mainWalletId(UUID.randomUUID().toString()).build();
        SubWallet subWallet = SubWallet.builder().subWalletName("Savings")
                .subWalletId(UUID.randomUUID().toString())
                .targetBalance(1500d).updatedAt(LocalDateTime.now()).build();

        when(validations.getUserInfo(anyString())).thenReturn(user);
        when(validations.getMainWalletInfo(anyString())).thenReturn(mainWallet);
        when(validations.getMainWalletByWalletId(anyString())).thenReturn(mainWallet);
        when(validations.findSubWalletIfExists(anyString(),anyString())).thenReturn(subWallet);

        MainWalletResponse response = updateSubWalletService.perform(request);
        assertNotNull(response);
    }

    @Test
    public void runUpdateTargetDateTest(){
        MainWalletRequest request = MainWalletRequest.builder().uuid(UUID.randomUUID().toString())
                .mainWalletId(UUID.randomUUID().toString())
                .subWalletId(UUID.randomUUID().toString())
                .actionType(UpdateWalletActionType.GOAL_DATE)
                .targetDate(LocalDate.of(2025,12,16))
                .requestType(RequestType.UPDATE).build();

        User user = User.builder().uuid(UUID.randomUUID().toString()).build();
        MainWallet mainWallet =  MainWallet.builder().mainWalletId(UUID.randomUUID().toString()).build();
        SubWallet subWallet = SubWallet.builder().subWalletName("Savings")
                .subWalletId(UUID.randomUUID().toString())
                .targetDate(LocalDate.of(2025,11,29)).updatedAt(LocalDateTime.now()).build();

        when(validations.getUserInfo(anyString())).thenReturn(user);
        when(validations.getMainWalletInfo(anyString())).thenReturn(mainWallet);
        when(validations.getMainWalletByWalletId(anyString())).thenReturn(mainWallet);
        when(validations.findSubWalletIfExists(anyString(),anyString())).thenReturn(subWallet);

        MainWalletResponse response = updateSubWalletService.perform(request);
        assertNotNull(response);

    }

    @Test
    public void runUpdateSubWalletWithoutSubWallet(){
        MainWalletRequest request = MainWalletRequest.builder().uuid(UUID.randomUUID().toString())
                .mainWalletId(UUID.randomUUID().toString())
                .subWalletId(UUID.randomUUID().toString())
                .actionType(UpdateWalletActionType.GOAL_AMOUNT)
                .targetBalance(2000d)
                .requestType(RequestType.UPDATE).build();

        User user = User.builder().uuid(UUID.randomUUID().toString()).build();
        MainWallet mainWallet =  MainWallet.builder().mainWalletId(UUID.randomUUID().toString()).build();

        when(validations.getUserInfo(anyString())).thenReturn(user);
        when(validations.getMainWalletInfo(anyString())).thenReturn(mainWallet);
        when(validations.getMainWalletByWalletId(anyString())).thenReturn(mainWallet);
        when(validations.findSubWalletIfExists(anyString(),anyString())).thenReturn(null);

//        MainWalletResponse response = updateSubWalletService.perform(request);
  assertThrows(CannotUpdateWalletException.class, ()->
          updateSubWalletService.perform(request));
    }

}
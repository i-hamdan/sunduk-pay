package com.bxb.sunduk_pay.factories.WalletFactory;

import com.bxb.sunduk_pay.exception.CannotUpdateWalletException;
import com.bxb.sunduk_pay.exception.InvalidPayloadException;
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
    void runUpdateTargetBalanceTest() {

        MainWalletRequest request = MainWalletRequest.builder()
                .uuid("user-1")
                .subWalletId("sub-1")
                .actionType(UpdateWalletActionType.GOAL_AMOUNT)
                .targetBalance(2000d)
                .requestType(RequestType.UPDATE)
                .build();

        User user = User.builder().uuid("user-1").build();
        MainWallet mainWallet = MainWallet.builder().mainWalletId("main-1").build();
        SubWallet subWallet = SubWallet.builder()
                .subWalletId("sub-1")
                .subWalletName("Savings")
                .targetBalance(1500d)
                .build();

        when(validations.getUserInfo(anyString())).thenReturn(user);
        when(validations.getMainWalletInfo(anyString())).thenReturn(mainWallet);
        when(validations.findSubWalletIfExists(anyString(), anyString()))
                .thenReturn(subWallet);

        MainWalletResponse response =
                updateSubWalletService.perform(request);

        assertNotNull(response);
        verify(mainWalletRepository).save(mainWallet);
    }

    @Test
    void runUpdateTargetDateTest() {

        MainWalletRequest request = MainWalletRequest.builder()
                .uuid("user-1")
                .subWalletId("sub-1")
                .actionType(UpdateWalletActionType.GOAL_DATE)
                .targetDate(LocalDate.of(2025, 12, 16))
                .requestType(RequestType.UPDATE)
                .build();

        User user = User.builder().uuid("user-1").build();
        MainWallet mainWallet = MainWallet.builder().mainWalletId("main-1").build();
        SubWallet subWallet = SubWallet.builder()
                .subWalletId("sub-1")
                .subWalletName("Savings")
                .targetDate(LocalDate.of(2025, 11, 29))
                .build();

        when(validations.getUserInfo(anyString())).thenReturn(user);
        when(validations.getMainWalletInfo(anyString())).thenReturn(mainWallet);
        when(validations.findSubWalletIfExists(anyString(), anyString()))
                .thenReturn(subWallet);

        MainWalletResponse response =
                updateSubWalletService.perform(request);

        assertNotNull(response);
        verify(mainWalletRepository).save(mainWallet);
    }

    @Test
    void runUpdateSubWalletWithoutSubWalletTargetBalance() {

        MainWalletRequest request = MainWalletRequest.builder()
                .uuid("user-1")
                .subWalletId("invalid-sub")
                .actionType(UpdateWalletActionType.GOAL_AMOUNT)
                .targetBalance(2000d)
                .requestType(RequestType.UPDATE)
                .build();

        User user = User.builder().uuid("user-1").build();
        MainWallet mainWallet = MainWallet.builder().mainWalletId("main-1").build();

        when(validations.getUserInfo(anyString())).thenReturn(user);
        when(validations.getMainWalletInfo(anyString())).thenReturn(mainWallet);
        when(validations.findSubWalletIfExists(anyString(), anyString()))
                .thenReturn(null);

        assertThrows(CannotUpdateWalletException.class,
                () -> updateSubWalletService.perform(request));

        verify(mainWalletRepository, never()).save(any());
    }

    @Test
    void updateSubWalletTargetDateWithoutSubWallet() {

        MainWalletRequest request = MainWalletRequest.builder()
                .uuid("user-1")
                .subWalletId("invalid-sub")
                .actionType(UpdateWalletActionType.GOAL_DATE)
                .targetDate(LocalDate.of(2025, 12, 16))
                .requestType(RequestType.UPDATE)
                .build();

        User user = User.builder().uuid("user-1").build();
        MainWallet mainWallet = MainWallet.builder().mainWalletId("main-1").build();

        when(validations.getUserInfo(anyString())).thenReturn(user);
        when(validations.getMainWalletInfo(anyString())).thenReturn(mainWallet);
        when(validations.findSubWalletIfExists(anyString(), anyString()))
                .thenReturn(null);

        assertThrows(CannotUpdateWalletException.class,
                () -> updateSubWalletService.perform(request));

        verify(mainWalletRepository, never()).save(any());
    }

    @Test
    void runUpdateSubWalletWithInvalidTargetBalance() {

        MainWalletRequest request = MainWalletRequest.builder()
                .uuid("user-1")
                .subWalletId("sub-1")
                .actionType(UpdateWalletActionType.GOAL_AMOUNT)
                .targetBalance(0d)
                .requestType(RequestType.UPDATE)
                .build();

        User user = User.builder().uuid("user-1").build();
        MainWallet mainWallet = MainWallet.builder().mainWalletId("main-1").build();
        SubWallet subWallet = SubWallet.builder()
                .subWalletId("sub-1")
                .subWalletName("Savings")
                .targetBalance(1500d)
                .build();

        when(validations.getUserInfo(anyString())).thenReturn(user);
        when(validations.getMainWalletInfo(anyString())).thenReturn(mainWallet);
        when(validations.findSubWalletIfExists(anyString(), anyString()))
                .thenReturn(subWallet);

        assertThrows(InvalidPayloadException.class,
                () -> updateSubWalletService.perform(request));

        verify(mainWalletRepository, never()).save(any());
    }


    @Test
    void updateSubWalletWithInvalidActionType(){
        MainWalletRequest request = MainWalletRequest.builder()
                .uuid("user-1")
                .subWalletId("sub-1")
                .actionType(UpdateWalletActionType.RENAME_POT)
                .targetBalance(0d)
                .requestType(RequestType.UPDATE)
                .build();


        User user = User.builder().uuid("user-1").build();
        MainWallet mainWallet = MainWallet.builder().mainWalletId("main-1").build();
        SubWallet subWallet = SubWallet.builder()
                .subWalletId("sub-1")
                .subWalletName("Savings")
                .targetBalance(1500d)
                .build();

        when(validations.getUserInfo(anyString())).thenReturn(user);
        when(validations.getMainWalletInfo(anyString())).thenReturn(mainWallet);
        when(validations.findSubWalletIfExists(anyString(), anyString()))
                .thenReturn(subWallet);


        assertThrows(InvalidPayloadException.class,
                ()->updateSubWalletService.perform(request));



    }
}
package com.bxb.sunduk_pay.factories.WalletFactory;

import com.bxb.sunduk_pay.exception.SubWalletAlreadyExistsException;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.repository.SubWalletRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSubWalletServiceTest {

    @Mock
    private Validations validations;

    @Mock
    private SubWalletRepository subWalletRepository;

    @Mock
    private MainWalletRepository mainWalletRepository;

    @InjectMocks
    private CreateSubwalletService createSubWalletService;


    @Test
    public void runTest(){

        MainWalletRequest request = MainWalletRequest.builder()
                .uuid("d0d529ce-80bc-42c9-b22c-577efe36ed18")
                .mainWalletId("2b5c0941-f7ad-4e6e-8486-cb04099ce5cd")
                .subWalletName("Bugatti")
                .targetBalance(2000d)
                .requestType(RequestType.CREATE)
                .targetDate(LocalDate.of(2025,12,19)).build();

        User user = User.builder().uuid("123").fullName("bilal").build();
        MainWallet mainWallet  = MainWallet
                .builder()
                .mainWalletId("123")
                .subWallets(new ArrayList<>())
                .build();

        when(validations.getUserInfo(anyString())).thenReturn(user);

        when(validations.getMainWalletInfo(anyString())).thenReturn(mainWallet);

        doNothing().when(validations).findSubWalletByName(anyString(),anyString());

        doNothing().when(validations).validateNumberOfSubWallets(anyInt());

        MainWalletResponse response = createSubWalletService.perform(request);
        assertNotNull(response);

        verify(subWalletRepository,times(1)).save(any());

        verify(mainWalletRepository,times(1)).save(any());

        assertEquals("Sub wallet created successfully",response.getMessage());

    }

    @Test
    public void runNegativeTest(){
        MainWalletRequest request = MainWalletRequest.builder()
                .uuid("d0d529ce-80bc-42c9-b22c-577efe36ed18")
                .mainWalletId("2b5c0941-f7ad-4e6e-8486-cb04099ce5cd")
                .subWalletName("savings")
                .targetBalance(2000d)
                .requestType(RequestType.CREATE)
                .targetDate(LocalDate.of(2025,12,19)).build();

        User user = User.builder().uuid("123").fullName("zaid").build();
        MainWallet mainwallet = MainWallet
                .builder().mainWalletId("321")
                .user(user)
                .subWallets(new ArrayList<>()).build();

        when(validations.getUserInfo(anyString())).thenReturn(user);
        when(validations.getMainWalletInfo(anyString())).thenReturn(mainwallet);

        doThrow(new SubWalletAlreadyExistsException("subWallet already exists"))
                .when(validations)
                .findSubWalletByName(anyString(),anyString());

        assertThrows(SubWalletAlreadyExistsException.class,
                ()-> createSubWalletService.perform(request));

        verify(subWalletRepository,never()).save(any());
        verify(mainWalletRepository,never()).save(any());



    }

}
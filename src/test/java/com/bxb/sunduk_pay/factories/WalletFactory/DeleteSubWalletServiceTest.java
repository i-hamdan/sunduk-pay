package com.bxb.sunduk_pay.factories.WalletFactory;

import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteSubWalletServiceTest {

    @Mock
    private Validations validations;
    @Mock
    private MainWalletRepository mainWalletRepository;
    @InjectMocks
    private DeleteSubWalletService deleteSubWalletService;

    @Test
    void deleteSubWalletTest() {

        MainWalletRequest request = MainWalletRequest
                .builder().mainWalletId(UUID.randomUUID().toString())
                .uuid(UUID.randomUUID().toString())
                .subWalletId(UUID.randomUUID().toString())
                .requestType(RequestType.DELETE)
                .build();

        User user = User.builder().uuid(UUID.randomUUID().toString())
                .fullName("zaid").build();

        MainWallet mainWallet = MainWallet.builder()
                .user(user).mainWalletId(UUID.randomUUID().toString())
                .subWallets(new ArrayList<>()).build();

        SubWallet subWallet = SubWallet.builder()
                .subWalletId(UUID.randomUUID().toString())
                .mainWallet(mainWallet)
                .subWalletName("car")
                .balance(0d)
                .isDeleted(false)
                .build();

        when(validations.getUserInfo(anyString())).thenReturn(user);

        when(validations.getMainWalletInfo(anyString())).thenReturn(mainWallet);

        when(validations.getMainWalletByWalletId(anyString())).thenReturn(mainWallet);

        when(validations.findSubWalletIfExists(anyString(),anyString())).thenReturn(subWallet);

        MainWalletResponse response = deleteSubWalletService.perform(request);

        assertNotNull(response);

        verify(mainWalletRepository,times(1)).save(mainWallet);

    }

    @Test
    public void deleteSubWalletWithNonZeroBalanceTest() {

        MainWalletRequest request = MainWalletRequest
                .builder().mainWalletId(UUID.randomUUID().toString())
                .uuid(UUID.randomUUID().toString())
                .subWalletId(UUID.randomUUID().toString())
                .requestType(RequestType.DELETE)
                .build();

        User user = User.builder().uuid(UUID.randomUUID().toString())
                .fullName("zaid").build();

        MainWallet mainWallet = MainWallet.builder()
                .user(user).mainWalletId(UUID.randomUUID().toString())
                .subWallets(new ArrayList<>()).build();

        SubWallet subWallet = SubWallet.builder()
                .subWalletId(UUID.randomUUID().toString())
                .mainWallet(mainWallet)
                .subWalletName("car")
                .balance(100d)
                .isDeleted(false)
                .build();

        when(validations.getUserInfo(anyString())).thenReturn(user);

        when(validations.getMainWalletInfo(anyString())).thenReturn(mainWallet);

        when(validations.getMainWalletByWalletId(anyString())).thenReturn(mainWallet);

        when(validations.findSubWalletIfExists(anyString(),anyString())).thenReturn(subWallet);

        assertThrows(Exception.class,()->{
            deleteSubWalletService.perform(request);
        });

    }

    @Test
    public void deleteNonExistentSubWalletTest() {

        MainWalletRequest request = MainWalletRequest
                .builder().mainWalletId(UUID.randomUUID().toString())
                .uuid(UUID.randomUUID().toString())
                .subWalletId(UUID.randomUUID().toString())
                .requestType(RequestType.DELETE)
                .build();

        User user = User.builder().uuid(UUID.randomUUID().toString())
                .fullName("zaid").build();

        MainWallet mainWallet = MainWallet.builder()
                .user(user).mainWalletId(UUID.randomUUID().toString())
                .subWallets(new ArrayList<>()).build();
        when(validations.getUserInfo(anyString())).thenReturn(user);
        when(validations.getMainWalletInfo(anyString())).thenReturn(mainWallet);
        when(validations.getMainWalletByWalletId(anyString())).thenReturn(mainWallet);
        when(validations.findSubWalletIfExists(anyString(),anyString())).thenReturn(null);

        assertThrows(Exception.class,()->{
            deleteSubWalletService.perform(request);
        });
}
}
package com.bxb.sunduk_pay.factories.globalPotFactory;

import com.bxb.sunduk_pay.Mappers.GlobalPotMapper;
import com.bxb.sunduk_pay.factories.GlobalPotFactory.AddContributorService;
import com.bxb.sunduk_pay.model.*;
import com.bxb.sunduk_pay.repository.*;
import com.bxb.sunduk_pay.request.GlobalPotRequest;
import com.bxb.sunduk_pay.response.GlobalPotResponse;
import com.bxb.sunduk_pay.util.GlobalPotRequestType;
import com.bxb.sunduk_pay.validations.GlobalPotValidations;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AddContributorServiceTest {

    @Mock
    private ContributerRepository contributerRepository;
    @Mock
    private GlobalPotValidations globalPotValidations;
    @Mock
    private GlobalPotMapper globalPotMapper;
    @Mock
    private Validations validations;
    @Mock
    private MainWalletRepository mainWalletRepository;
    @Mock
    private MasterWalletRepository  masterWalletRepository;
    @Mock
    private GlobalWalletRepository globalWalletRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private SubWalletRepository subWalletRepository;
    @Mock
    private GlobalPotRepository globalPotRepository;
    @InjectMocks
    private AddContributorService addContributorService;


    @Test
    void shouldReturnAddContributorRequestType(){
        GlobalPotRequestType type = addContributorService.getGlobalPotRequestType();
        Assertions.assertEquals(type,GlobalPotRequestType.ADD_CONTRIBUTOR);
    }



    @Test
    void shouldAddContributorSuccessfully_fromSubWallet() throws Exception {

        // GIVEN
        GlobalPotRequest request = new GlobalPotRequest();
        request.setUserContributorId("user-1");
        request.setGlobalPotId("pot-1");
        request.setAmountContributed(200.0);
        request.setSourceWalletId("SUB-1");

        User user = new User();
        user.setUuid("user-1");

        GlobalPot globalPot = new GlobalPot();
        globalPot.setGlobalPotId("pot-1");
        globalPot.setContributedBalance(300.0);

        MasterWallet masterWallet = new MasterWallet();
        masterWallet.setBalance(1000.0);

        GlobalWallet globalWallet = new GlobalWallet();
        globalWallet.setGlobalWalletId("GW-1");
        globalWallet.setBalance(500.0);
        globalPot.setGlobalWallet(globalWallet);

        MainWallet mainWallet = new MainWallet();
        mainWallet.setMainWalletId("MAIN-1");

        SubWallet subWallet = new SubWallet();
        subWallet.setSubWalletId("SUB-1");
        subWallet.setBalance(500.0);

        Contributor contributor = new Contributor();

        // STUBS
        when(validations.getUserInfo("user-1"))
                .thenReturn(user);

        when(globalPotValidations.getGlobalPot("pot-1"))
                .thenReturn(globalPot);

        when(masterWalletRepository.findByUserUuid("user-1"))
                .thenReturn(Optional.of(masterWallet));

        doNothing().when(validations)
                .validateBalance(anyDouble(), anyDouble());

        when(globalWalletRepository.findById("GW-1"))
                .thenReturn(Optional.of(globalWallet));

        when(mainWalletRepository.findByUserUuid("user-1"))
                .thenReturn(Optional.of(mainWallet));


        when(validations.findSubWalletIfExists("MAIN-1","SUB-1"))
                .thenReturn(subWallet);

        when(globalPotMapper.toContributerEntity(request, globalPot))
                .thenReturn(contributor);

        // WHEN
        GlobalPotResponse response =
                addContributorService.perform(request);

        // THEN
        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "Contributor added successfully",
                response.getMessage()
        );

//        verify(subWalletRepository).save(subWallet);
//        verify(masterWalletRepository).save(masterWallet);
//        verify(globalWalletRepository).save(globalWallet);
//        verify(transactionRepository).saveAll(anyList());
//        verify(globalPotRepository).save(globalPot);
//        verify(contributerRepository).save(contributor);
    }


    @Test
    void shouldAddContributorSuccessfully_fromMainWallet_ELSE_BRANCH() throws Exception {

        // GIVEN
        GlobalPotRequest request = new GlobalPotRequest();
        request.setUserContributorId("user-1");
        request.setGlobalPotId("pot-1");
        request.setAmountContributed(300.0);
        request.setSourceWalletId("MAIN-1");

        User user = new User();
        user.setUuid("user-1");

        GlobalPot globalPot = new GlobalPot();
        globalPot.setGlobalPotId("pot-1");
        globalPot.setContributedBalance(200.0);

        MasterWallet masterWallet = new MasterWallet();
        masterWallet.setBalance(1000.0);

        GlobalWallet globalWallet = new GlobalWallet();
        globalWallet.setGlobalWalletId("GW-1");
        globalWallet.setBalance(500.0);
        globalPot.setGlobalWallet(globalWallet);

        MainWallet mainWallet = new MainWallet();
        mainWallet.setMainWalletId("MAIN-1");
        mainWallet.setBalance(800.0);

        Contributor contributor = new Contributor();

        // STUBS
        when(validations.getUserInfo("user-1"))
                .thenReturn(user);

        when(globalPotValidations.getGlobalPot("pot-1"))
                .thenReturn(globalPot);

        when(masterWalletRepository.findByUserUuid("user-1"))
                .thenReturn(Optional.of(masterWallet));

        doNothing().when(validations)
                .validateBalance(anyDouble(), anyDouble());

        when(globalWalletRepository.findById("GW-1"))
                .thenReturn(Optional.of(globalWallet));

        when(mainWalletRepository.findByUserUuid("user-1"))
                .thenReturn(Optional.of(mainWallet));


        when(validations.findSubWalletIfExists("MAIN-1","MAIN-1"))
                .thenReturn(null);

        when(globalPotMapper.toContributerEntity(request, globalPot))
                .thenReturn(contributor);

        // WHEN
        GlobalPotResponse response =
                addContributorService.perform(request);

        // THEN
        Assertions.assertNotNull(response);
        Assertions.assertEquals(
                "Contributor added successfully",
                response.getMessage()
        );

        verify(mainWalletRepository).save(mainWallet);
        verify(masterWalletRepository).save(masterWallet);
        verify(globalWalletRepository).save(globalWallet);
        verify(transactionRepository).saveAll(anyList());
        verify(globalPotRepository).save(globalPot);
        verify(contributerRepository).save(contributor);
    }
    
}
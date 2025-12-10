package com.bxb.sunduk_pay.factories.WalletFactory;

import com.bxb.sunduk_pay.exception.SubWalletAlreadyExistsException;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.SubWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.repository.SubWalletRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CreateServiceTest {

    @Autowired
    private Validations validations;
    @Autowired
    private SubWalletRepository subWalletRepository;
    @Autowired
    private MainWalletRepository mainWalletRepository;
    @Autowired
    private CreateService createSubWalletService;


    @Test
    public void runTest(){

        MainWalletRequest request = MainWalletRequest.builder()
                .uuid("d0d529ce-80bc-42c9-b22c-577efe36ed18")
                .mainWalletId("2b5c0941-f7ad-4e6e-8486-cb04099ce5cd")
                .subWalletName("private jet")
                .targetBalance(2000d)
                .requestType(RequestType.CREATE)
                .targetDate(LocalDate.of(2025,12,19)).build();

        MainWalletResponse response = createSubWalletService.perform(request);
        assertNotNull(response);
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

        assertThrows(SubWalletAlreadyExistsException.class,
                ()-> createSubWalletService.perform(request));

    }

}
package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.Mappers.TransactionMapper;
import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.MasterWallet;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.MainWalletRepository;
import com.bxb.sunduk_pay.repository.MasterWalletRepository;
import com.bxb.sunduk_pay.repository.TransactionRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.xml.validation.Validator;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class ExternalTransferServiceImplTest {

    @Mock
    private MasterWalletRepository masterWalletRepository;

    @Mock
    private MainWalletRepository mainWalletRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private Validations validations;

    @InjectMocks
    private ExternalTransferServiceImpl externalTransferServiceImpl;

    @Test
    void shouldTransferAmountSuccessfully_whenValidUPIRequest() {

        /**
         * Given
         */
        MainWalletRequest request = new MainWalletRequest();
        request.setUuid("user-123");
        request.setAmount(500.0);
        request.setRecipientUpiId("test@upi");
        request.setPaymentTag("UPI-PAY");

        User user = new User();
        user.setUuid("user-123");

        MainWallet mainWallet = new MainWallet();
        mainWallet.setMainWalletId("MAIN-1");
        mainWallet.setBalance(500.0);

        MasterWallet  masterWallet = new MasterWallet();
        masterWallet.setMasterWalletId("MASTER-1");
        masterWallet.setBalance(500.0);

        /**
         * stub
         */
        doNothing().when(validations)
                .validateRecipientUpiId("test@upi");

        when(validations.getUserInfo("test@upi"))
                .thenReturn(user);


    }
}
package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.factories.WalletFactory.AddReminderService;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.ReminderRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.Duration;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddReminderServiceImpl {
    @Mock
    private Validations validations;

    @Mock
    private ReminderRepository reminderRepository;

    @InjectMocks
    private AddReminderService addReminderService;

    @Test
    public void AddReminder (){

        MainWalletRequest request = MainWalletRequest.builder()
                .amount(200.0)
                .startDate(LocalDate.now())
                .duration(Duration.WEEKLY)
                .contactName("ayan")
                .contactNumber("+919123456787")
                .uuid("6a7d302c-19ee-45d5-ac49-47f1fd97760f")
                .requestType(RequestType.ADD_REMINDER).build();

        User user = new User();
        user.setUuid("6a7d302c-19ee-45d5-ac49-47f1fd97760f");
        when(validations.getUserInfo(anyString())).thenReturn(user);

        MainWalletResponse response = addReminderService.perform(request);

        assertNotNull(response);

        verify(reminderRepository, times(1)).save(any());
    }

}

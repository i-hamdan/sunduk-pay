package com.bxb.sunduk_pay.factories.WalletFactory;

import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.model.User;
import com.bxb.sunduk_pay.repository.ReminderRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.Duration;
import com.bxb.sunduk_pay.util.RequestType;
import com.bxb.sunduk_pay.validations.Validations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateReminderServiceTest {

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private Validations validations;

    @InjectMocks
    private UpdateReminderService updateReminderService;

    //Request Type Test
    @Test
    void shouldReturnUpdateReminderRequestType(){
        RequestType requestType = RequestType.UPDATE_REMINDER;
        Assertions.assertEquals(
                requestType,updateReminderService.getRequestType());
    }

    // Successfully Update Reminder Test
    @Test
    void shouldUpdateReminderSuccessfully(){

        // Given
        MainWalletRequest mainWalletRequest = new MainWalletRequest();
        mainWalletRequest.setUuid("user-1");
        mainWalletRequest.setReminderId("r-1");
        mainWalletRequest.setAmount(500.0);
        mainWalletRequest.setRemark("Update Remark");
        mainWalletRequest.setDuration(Duration.MONTHLY);
        mainWalletRequest.setAutoPayEnabled(true);

        User user = new User();
        user.setUuid("user-1");

        Reminder reminder = new Reminder();
        reminder.setReminderId("r-1");
        reminder.setUser(user);
        reminder.setAmount(500.0);
        reminder.setDuration(Duration.WEEKLY);
        reminder.setRemark("Old Remark");
        reminder.setAutoPayEnabled(false);

        // stubs
        when(validations.getUserInfo("user-1"))
                .thenReturn(user);

        when(validations.getReminderById("r-1"))
                .thenReturn(reminder);

        // When
        MainWalletResponse response =
                updateReminderService.perform(mainWalletRequest);

        // Then
        Assertions.assertNotNull(response);
        Assertions.assertEquals("SUCCESS", response.getStatus());
        Assertions.assertEquals("Reminder updated successfully.",
                response.getMessage());

        // Verify Save
        verify(reminderRepository).save(reminder);

        // field-level validation after update
        Assertions.assertEquals(500.0, reminder.getAmount());
        Assertions.assertEquals(Duration.MONTHLY, reminder.getDuration());
        Assertions.assertEquals("Update Remark", reminder.getRemark());
        Assertions.assertTrue(reminder.getAutoPayEnabled());

    }

    // Unauthorized Update Reminder Test
    @Test
    void shouldFailIfReminderDoesNotBelongToUser(){

        // Given
        MainWalletRequest mainWalletRequest = new MainWalletRequest();
        mainWalletRequest.setUuid("user-1");
        mainWalletRequest.setReminderId("r-1");

        User loggedInUser = new User();
        loggedInUser.setUuid("user-1");

        User differentUser = new User();
        differentUser.setUuid("user-2");

        Reminder reminder = new Reminder();
        reminder.setReminderId("r-1");
        reminder.setUser(differentUser); // Reminder belongs to different user

        // stubs

        when(validations.getUserInfo("user-1"))
                .thenReturn(loggedInUser);

        when(validations.getReminderById("r-1"))
                .thenReturn(reminder);

        // When
        MainWalletResponse response =
                updateReminderService.perform(mainWalletRequest);

        // Then
        Assertions.assertNotNull(response);
        Assertions.assertEquals("FAILED", response.getStatus());
        Assertions.assertEquals("Unauthorized to update this reminder.",
                response.getMessage());

        verify(reminderRepository, never()).save(reminder);

    }

}

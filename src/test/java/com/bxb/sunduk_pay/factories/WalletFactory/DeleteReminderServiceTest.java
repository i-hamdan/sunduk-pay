package com.bxb.sunduk_pay.factories.WalletFactory;


import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.repository.ReminderRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.util.RequestType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteReminderServiceTest {

    @Mock
    private  ReminderRepository reminderRepository;

    @InjectMocks
    private DeleteReminderService deleteReminderService;

    @Test
    public void testDeleteReminderRequestType() {
            RequestType requestType = deleteReminderService.getRequestType();
            assertNotNull(requestType);
            assert(requestType == RequestType.DELETE_REMINDER);
        }

        @Test
        public void shouldDeleteReminderSuccessfully() {

           MainWalletRequest request = new MainWalletRequest();
           request.setReminderId("cc663eb8-8b12-469d-a6e2-fd0891a86004");

           Reminder reminder = new Reminder();
           when(reminderRepository.findByReminderId(request.getReminderId()))
                   .thenReturn(reminder);

              MainWalletResponse response = deleteReminderService.perform(request);

              assertNotNull(response);
              assertEquals("SUCCESS", response.getStatus());
              assertEquals("Reminder deleted successfully", response.getMessage());
              verify(reminderRepository, times(1)).delete(reminder);

        }

        @Test
        public void shouldReturnFailureWhenReminderNotFound() {

        MainWalletRequest request = new MainWalletRequest();
        request.setReminderId("cc663eb8-8b12-469d-a6e2-fd0891a86004");

        when(reminderRepository.findByReminderId(request.getReminderId()))
                .thenReturn(null);

        MainWalletResponse response = deleteReminderService.perform(request);

        assertNotNull(response);
        assertEquals("FAILURE", response.getStatus());
        assertEquals("Reminder not found", response.getMessage());
        verify(reminderRepository, never()).delete(any());

        }

        @Test
    public void shouldReturnFailureWhenExceptionOccurs(){
        MainWalletRequest request = new MainWalletRequest();
        request.setReminderId("cc663eb8-8b12-469d-a6e2-fd0891a86004");

        when(reminderRepository.findByReminderId(request.getReminderId()))
                .thenThrow(new RuntimeException("Database error"));

        MainWalletResponse response = deleteReminderService.perform(request);

        assertNotNull(response);
        assertEquals("FAILURE", response.getStatus());
        assertEquals("Unexpected error occurred while deleting reminder",response.getMessage());
        verify(reminderRepository, never()).delete(any());

        }
}

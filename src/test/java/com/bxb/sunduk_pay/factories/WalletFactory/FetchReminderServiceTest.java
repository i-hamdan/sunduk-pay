package com.bxb.sunduk_pay.factories.WalletFactory;

import com.bxb.sunduk_pay.Mappers.ReminderMapper;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FetchReminderServiceTest {

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private ReminderMapper reminderMapper;

    @InjectMocks
    private FetchReminderService fetchReminderService;

    @Test
    public void testPerform() {
        RequestType requestType = fetchReminderService.getRequestType();
        assertNotNull(requestType);
        assert(requestType == RequestType.FETCH_REMINDERS);
    }

    @Test
    public void fetchReminderSuccessfully(){

        MainWalletRequest request = new MainWalletRequest();
        request.setPage(0);
        request.setSize(10);
        request.setSortDirection("ASC");
        request.setUuid("1eab9e05-4c63-45f3-88c8-2a366cbeec11");
        request.setContactNumber("91234567890");
        request.setSortBy("startDate");

        Reminder reminder = new Reminder();

        when(reminderRepository.findByUserUuidAndContactNumber(
                request.getUuid(),
                request.getContactNumber(),
                PageRequest.of(0,10, Sort.Direction.ASC, "startDate")
        )).thenReturn(List.of(reminder));

        MainWalletResponse response = fetchReminderService.perform(request);

        assertNotNull(response);
        assertNotNull(response.getReminders());
        assertEquals(1, response.getReminders().size());

        verify(reminderRepository, times(1)).findByUserUuidAndContactNumber(
                request.getUuid(),
                request.getContactNumber(),
                PageRequest.of(0,10, Sort.Direction.ASC, "startDate")
        );

        verify(reminderMapper, times(1)).toReminderResponse(reminder);
    }
}

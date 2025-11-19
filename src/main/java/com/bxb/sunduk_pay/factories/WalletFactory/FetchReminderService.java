package com.bxb.sunduk_pay.factories.WalletFactory;

import com.bxb.sunduk_pay.Mappers.ReminderMapper;
import com.bxb.sunduk_pay.model.Reminder;
import com.bxb.sunduk_pay.repository.ReminderRepository;
import com.bxb.sunduk_pay.request.MainWalletRequest;
import com.bxb.sunduk_pay.response.MainWalletResponse;
import com.bxb.sunduk_pay.response.ReminderResponse;
import com.bxb.sunduk_pay.util.RequestType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Service class for fetching reminders associated
 * with a main wallet.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FetchReminderService implements WalletOperation{

    /** Repository for accessing reminder data. */
    private final ReminderRepository reminderRepository;

    /** Mapper to convert Reminder entities to ReminderResponse DTOs. */
    private final ReminderMapper reminderMapper;
    /**
     * @return FETCH_REMINDERS request type.
     */
    @Override
    public RequestType getRequestType() {
        return RequestType.FETCH_REMINDERS;
    }

    /**
     * @param mainWalletRequest the request containing
     *                          necessary data for the operation.
     * @return
     */
    @Override
    public MainWalletResponse perform(MainWalletRequest mainWalletRequest) {

            Sort.Direction direction;
            if ("ASC".equalsIgnoreCase(mainWalletRequest.getSortDirection())) {
                direction = Sort.Direction.ASC;
            } else {
                direction = Sort.Direction.DESC;
            }

            Pageable pageable = PageRequest.of(mainWalletRequest.getPage(),
                    mainWalletRequest.getSize(),
                    Sort.by(direction, mainWalletRequest.getSortBy()));

        Page<Reminder> reminderPage =
                reminderRepository.findByContactNumber(mainWalletRequest.getContactNumber(), pageable);

        List<ReminderResponse> reminderResponses =
                reminderPage.getContent().stream()
                        .map(reminderMapper::toReminderResponse)
                        .toList();
        log.info("Fetched {} reminders for contact number: {}",
                reminderResponses.size(), mainWalletRequest.getContactNumber());

        return MainWalletResponse.builder()
                .reminders(reminderResponses)
                .build();
    }
}

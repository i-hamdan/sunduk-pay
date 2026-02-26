package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.exception.ResourceNotFoundException;
import com.bxb.sunduk_pay.factories.notificationPreferenceFactory.NotificationPreferenceOperation;
import com.bxb.sunduk_pay.factories.notificationPreferenceFactory.NotificationPreferenceOperationFactory;
import com.bxb.sunduk_pay.request.NotificationPreferenceRequest;
import com.bxb.sunduk_pay.response.NotificationPreferenceResponse;
import com.bxb.sunduk_pay.service.NotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Implementation of the NotificationPreferenceService interface for managing
 * notification preferences.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationPreferenceServiceImpl
        implements NotificationPreferenceService {

    /** Factory to create operations for handling notification preferences. */
    private final NotificationPreferenceOperationFactory operationFactory;

    /** Handles the notification preference API request and returns a
     *  response. */
    @Override
    public NotificationPreferenceResponse preferenceApi(
            final NotificationPreferenceRequest request) {

        NotificationPreferenceOperation operation = operationFactory.
                getOperation(request.getPreferenceRequestType());

        if (operation == null){
            throw new ResourceNotFoundException("Invalid request type: "
                    + request.getPreferenceRequestType());
        }
        return operation.perform(request);
    }
}

package com.bxb.sunduk_pay.service;

import com.bxb.sunduk_pay.request.NotificationPreferenceRequest;
import com.bxb.sunduk_pay.response.NotificationPreferenceResponse;

/** Service class for handling notification preference operations. */
public interface NotificationPreferenceService {
    /** Processes the notification preference request and returns the corresponding response. */
    public NotificationPreferenceResponse preferenceApi(
            NotificationPreferenceRequest request);
}

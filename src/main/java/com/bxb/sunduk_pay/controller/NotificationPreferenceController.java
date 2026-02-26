package com.bxb.sunduk_pay.controller;

import com.bxb.sunduk_pay.model.NotificationPreference;
import com.bxb.sunduk_pay.request.NotificationPreferenceRequest;
import com.bxb.sunduk_pay.response.NotificationPreferenceResponse;
import com.bxb.sunduk_pay.service.NotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** Controller for managing user notification preferences. */
@RestController
@RequiredArgsConstructor
public class NotificationPreferenceController {

    /** Service to handle notification preference operations. */
    private final NotificationPreferenceService notificationPreferenceService;

    @PostMapping("/notification-preference")
    public ResponseEntity<NotificationPreferenceResponse> preferenceApi(
            @RequestBody final NotificationPreferenceRequest request){
        return ResponseEntity.ok(notificationPreferenceService
                .preferenceApi(request));
    }
}

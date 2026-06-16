package com.bxb.sunduk_pay.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.apache.catalina.LifecycleState;

import java.util.List;

/** Response class for user notification preferences. */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationPreferenceResponse {
    /**
     * The unique identifier of the user for whom the notification preference
     * is being set.
     */
    private Boolean walletNotifications;

    /**
     * The unique identifier of the notification preference being set.
     */
    private Boolean potReminders;

    /**
     * The unique identifier of the notification preference being set.
     */
    private Boolean scheduleReminders;

    /**
     * The unique identifier of the notification preference being set.
     */
    private Boolean promotionalNotifications;

    /**
     * The unique identifier of the notification preference being set.
     */
    private Boolean systemAlerts;

    /**
     * The unique identifier of the notification preference being set.
     */
    private List<String> updatedFields;

    /**
     * The unique identifier of the notification preference being set.
     */
    private String message; // Optional (used mainly for UPDATE responses)
}

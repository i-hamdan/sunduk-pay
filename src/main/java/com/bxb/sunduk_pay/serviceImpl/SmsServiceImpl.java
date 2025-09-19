package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.config.TwilioConfig;
import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.exceptions.SmsServiceException;
import com.bxb.sunduk_pay.service.SmsService;
import com.bxb.sunduk_pay.util.FallbackEmailUtil;
import com.bxb.sunduk_pay.util.SmsMessageUtil;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service implementation for sending SMS using Twilio.
 * Falls back to sending emails if SMS fails.
 */
@Service
@Log4j2
public final class SmsServiceImpl implements SmsService {

    /** Twilio configuration. */
    private final TwilioConfig twilioConfig;

    /** Utility to build SMS messages. */
    private final SmsMessageUtil smsMessageUtil;

    /** Utility to send fallback emails. */
    private final FallbackEmailUtil fallbackEmailUtil;

    /**
     * Constructs the SmsServiceImpl.
     *
     * @param twilioConfig      Twilio configuration
     * @param smsMessageUtil    SMS message utility
     * @param fallbackEmailUtil fallback email utility
     */
    public SmsServiceImpl(final TwilioConfig twilioConfig,
                          final SmsMessageUtil smsMessageUtil,
                          final FallbackEmailUtil fallbackEmailUtil) {
        this.twilioConfig = twilioConfig;
        this.smsMessageUtil = smsMessageUtil;
        this.fallbackEmailUtil = fallbackEmailUtil;
    }

    /**
     * Initializes Twilio SDK after bean creation.
     */
    @PostConstruct
    public void initTwilio() {
        try {
            Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
            log.info("Twilio initialized successfully.");
        } catch (final Exception e) {
            log.error("Error initializing Twilio: {}", e.getMessage(), e);
            throw new SmsServiceException("Twilio initialization failed.");
        }
    }

    /**
     * Processes a transaction event to send SMS notification.
     * Falls back to email if SMS sending fails.
     *
     * @param event transaction event
     */
    @Override
    public void processSmsEvent(final TransactionEvent event) {
        final String message = smsMessageUtil.buildTransactionSms(event);
        try {
            sendSms(event.getPhoneNumber(), message);
            log.info("SMS sent successfully for transaction ID: {}", event.getTransactionId());
        } catch (final SmsServiceException e) {
            log.error("SMS sending failed for transaction ID {}: {}",
                    event.getTransactionId(), e.getMessage());
            fallbackEmailUtil.sendFallbackTransactionEmail(event);
            log.info("Fallback email triggered for transaction ID: {}", event.getTransactionId());
        }
    }

    /**
     * Sends an SMS to the specified phone number.
     *
     * @param to      recipient phone number
     * @param message message content
     * @throws SmsServiceException if SMS sending fails
     */
    public void sendSms(final String to, final String message) {
        try {
            Message.creator(new PhoneNumber(to),
                            new PhoneNumber(twilioConfig.getFromNumber()),
                            message)
                    .create();
            log.debug("SMS successfully created for {}", to);
        } catch (final Exception e) {
            log.error("Failed to send SMS to {}: {}", to, e.getMessage(), e);
            throw new SmsServiceException("Failed to send SMS to: " + to);
        }
    }
}

package com.bxb.sunduk_pay.serviceImpl;


import com.bxb.sunduk_pay.config.TwilioConfig;
import com.bxb.sunduk_pay.exception.SmsServiceException;
import com.bxb.sunduk_pay.kafkaEvents.OtpEvent;
import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.service.SmsService;
import com.bxb.sunduk_pay.util.FallbackEmailUtil;
import com.bxb.sunduk_pay.util.SmsMessageUtil;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service implementation for sending SMS using Twilio.
 * Falls back to sending emails if SMS fails.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class SmsServiceImpl implements SmsService {
    /**
     * Configuration for Twilio API.
     */
    private final TwilioConfig twilioConfig;
    /**
     * Utility for building SMS messages.
     */
    private final SmsMessageUtil smsMessageUtil;
    /**
     * Utility for sending fallback emails.
     */
    private final FallbackEmailUtil fallbackEmailUtil;


    /**
     * Initializes Twilio SDK after bean creation.
     */
    @PostConstruct
    public void initTwilio() {
        try {
            Twilio.init(
                    twilioConfig.getAccountSid(),
                    twilioConfig.getAuthToken());
            log.info("Twilio initialized successfully.");
        } catch (Exception e) {
            log.error(
                    "Error initializing Twilio: {}", e.getMessage());
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
        String message = smsMessageUtil.buildTransactionSms(event);
        try {
            sendSms(event.getPhoneNumber(), message);
            log.info("SMS sent for Txn ID: {}", event.getTransactionId());
        } catch (SmsServiceException e) {
            log.error("SMS failed for Txn ID {}: {}", event.getTransactionId(),
                    e.getMessage());
            fallbackEmailUtil.sendFallbackTransactionEmail(event);
        }

    }

    @Override
    public void processOtpEvent(OtpEvent otpEvent) {

        String message = smsMessageUtil.buildOtpSms(otpEvent.getOtp());
        sendSms(otpEvent.getPhoneNumber(), message);
        log.info("OTP SMS sent to: {}", otpEvent.getPhoneNumber());

    }

    /**
     * Sends an SMS to the specified phone number.
     *
     * @param to      recipient phone number
     * @param message message content
     * @throws SmsServiceException if SMS sending fails.
     */
    public void sendSms(final String to, final String message) {
        try {
            Message.creator(new PhoneNumber(to),
                    new PhoneNumber(twilioConfig.getFromNumber()),
                    message
            ).create();
        } catch (Exception e) {
            log.error("Error while sending SMS to {}: {}",
                    to, e.getMessage());
            throw new SmsServiceException("Failed to send SMS to: " + to);
        }
    }
}

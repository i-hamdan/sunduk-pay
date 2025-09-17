package com.bxb.sunduk_pay.serviceImpl;

import com.bxb.sunduk_pay.config.TwilioConfig;
import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.exception.SmsServiceException;
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
 * Also triggers fallback emails when SMS fails.
 */
@Service
@Log4j2
public class SmsServiceImpl implements SmsService {

    private final TwilioConfig twilioConfig;
    private final SmsMessageUtil smsMessageUtil;
    private final FallbackEmailUtil fallbackEmailUtil;

    public SmsServiceImpl(TwilioConfig twilioConfig,
                          SmsMessageUtil smsMessageUtil,
                          FallbackEmailUtil fallbackEmailUtil) {
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
        } catch (Exception e) {
            log.error("Error initializing Twilio: {}", e.getMessage(), e);
            throw new SmsServiceException("Twilio initialization failed.");
        }
    }

    /**
     * Processes a transaction event to send SMS notification.
     * If SMS fails, a fallback email is triggered.
     *
     * @param event the transaction event containing SMS details
     */
    @Override
    public void processSmsEvent(TransactionEvent event) {
        String message = smsMessageUtil.buildTransactionSms(event);
        try {
            sendSms(event.getPhoneNumber(), message);
            log.info("SMS sent successfully for transaction ID: {}", event.getTransactionId());
        } catch (SmsServiceException e) {
            log.error("SMS sending failed for transaction ID {}: {}", event.getTransactionId(), e.getMessage());
            fallbackEmailUtil.sendFallbackTransactionEmail(event);
            log.info("Fallback email triggered for transaction ID: {}", event.getTransactionId());
        }
    }

    /**
     * Sends an SMS to the given phone number.
     *
     * @param to      recipient phone number
     * @param message message content
     * @throws SmsServiceException if sending SMS fails
     */
    public void sendSms(String to, String message) {
        try {
            Message.creator(new PhoneNumber(to),
                            new PhoneNumber(twilioConfig.getFromNumber()),
                            message)
                    .create();
            log.debug("SMS successfully created for {}", to);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", to, e.getMessage(), e);
            throw new SmsServiceException("Failed to send SMS to: " + to);
        }
    }
}

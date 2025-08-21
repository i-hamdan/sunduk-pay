package com.bxb.sunduk_pay.serviceImpl;


import com.bxb.sunduk_pay.config.TwilioConfig;
import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.exception.SmsServiceException;
import com.bxb.sunduk_pay.service.EmailService;
import com.bxb.sunduk_pay.service.SmsService;
import com.bxb.sunduk_pay.util.FallbackEmailUtil;
import com.bxb.sunduk_pay.util.SmsMessageUtil;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class SmsServiceImpl implements SmsService {
    private final TwilioConfig twilioConfig;
    private final SmsMessageUtil smsMessageUtil;
    private final FallbackEmailUtil fallbackEmailUtil;


    public SmsServiceImpl(TwilioConfig twilioConfig, SmsMessageUtil smsMessageUtil, FallbackEmailUtil fallbackEmailUtil) {
        this.twilioConfig = twilioConfig;
        this.smsMessageUtil = smsMessageUtil;
        this.fallbackEmailUtil = fallbackEmailUtil;
    }

    @PostConstruct
    public void initTwilio() {
        try {
            Twilio.init(twilioConfig.getAccountSid(),twilioConfig.getAuthToken());
            log.info("Twilio initialized successfully.");
        } catch (Exception e) {
            log.error("Error initializing Twilio: {}", e.getMessage());
            throw new SmsServiceException("Twilio initialization failed.");
        }
    }

    @Override
    public void processSmsEvent(TransactionEvent event) {
 String message = smsMessageUtil.buildTransactionSms(event);
 try {
     sendSms(event.getPhoneNumber(), message);
     log.info("SMS sent for Txn ID: {}", event.getTransactionId());
 } catch (SmsServiceException e) {
     log.error("SMS failed for Txn ID {}: {}", event.getTransactionId(), e.getMessage());
     fallbackEmailUtil.sendFallbackTransactionEmail(event);
 }

 }


    public void sendSms(String to,String message){
        try {
            Message.creator(new PhoneNumber(to), new PhoneNumber(twilioConfig.getFromNumber()), message).create();
        } catch (Exception e) {
            log.error("Error while sending SMS to {}: {}", to, e.getMessage());
            throw new SmsServiceException("Failed to send SMS to: " + to);
        }    }



}

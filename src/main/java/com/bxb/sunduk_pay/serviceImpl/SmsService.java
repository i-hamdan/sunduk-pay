package com.bxb.sunduk_pay.serviceImpl;


import com.bxb.sunduk_pay.config.TwilioConfig;
import com.bxb.sunduk_pay.event.TransactionEvent;
import com.bxb.sunduk_pay.exception.SmsServiceException;
import com.bxb.sunduk_pay.service.EmailService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class SmsService {
    private final TwilioConfig twilioConfig;
    private final EmailService emailService;

    public SmsService(TwilioConfig twilioConfig, EmailService emailService) {
        this.twilioConfig = twilioConfig;
        this.emailService = emailService;
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

    @KafkaListener(topics = "transaction-topic", groupId = "sms-service-group",concurrency = "3")
    public void consumeTransactionEvent(TransactionEvent transactionEvent){
        try {
            String message = "Assalamualaikum " + transactionEvent.getFullName() + ", your "
                    + transactionEvent.getTransactionType() + " of " + transactionEvent.getAmount()
                    + " is done. Txn ID: " + transactionEvent.getTransactionId()
                    + ". Bal: ₹" + transactionEvent.getRemainingAmount() + ". - SundukPay";
            sendSms(transactionEvent.getPhoneNumber(), message);
            log.info("SMS sent successfully for Txn ID: {}", transactionEvent.getTransactionId());

        } catch (Exception e) {
            log.error("Failed to send SMS for Txn ID {}: {}", transactionEvent.getTransactionId(), e.getMessage());
            try {
                String subject = "[Transaction Alert] ₹" + transactionEvent.getAmount() + " " + transactionEvent.getTransactionType() + " on Wallet ID: " + transactionEvent.getWalletId();
                String body = "Dear " + transactionEvent.getFullName() + ",\n\n" +
                        "We attempted to send you an SMS regarding your transaction, but it failed.\n\n" +
                        "🔹 Transaction Details\n" +
                        "- Transaction ID: " + transactionEvent.getTransactionId() + "\n" +
                        "- Wallet ID: " + transactionEvent.getWalletId() + "\n" +
                        "- Amount: ₹" + transactionEvent.getAmount() + "\n" +
                        "- Transaction Type: " + transactionEvent.getTransactionType() + "\n" +
                        "- Date & Time: " + transactionEvent.getDateTime() + "\n" +
                        "- Remaining Wallet Balance: ₹" + transactionEvent.getRemainingAmount() + "\n\n" +
                        "Thank you for using SundukPay.\n\n" +
                        "Warm regards,\n" +
                        "SundukPay Team";

                emailService.sendEmail(transactionEvent.getEmail(), subject, body);
                log.info("Fallback email sent for failed SMS to: {}", transactionEvent.getEmail());

            } catch (Exception emailFallbackEx) {
                log.error("Failed to send fallback email for Txn ID {}: {}", transactionEvent.getTransactionId(), emailFallbackEx.getMessage());
                throw new SmsServiceException("Failed to send SMS for transaction: " + transactionEvent.getTransactionId());
            }

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

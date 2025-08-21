package com.bxb.sunduk_pay.util;

import com.bxb.sunduk_pay.exception.SmsServiceException;
import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.service.EmailService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class FallbackEmailUtil {
    private final EmailService emailService;

    public FallbackEmailUtil(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendFallbackTransactionEmail( TransactionEvent event) {
        try {
            String subject = "[Transaction Alert] SundukPay – "
                    + event.getTransactionType()
                    + " of $" + String.format("%.2f", event.getAmount());

            // Trim IDs safely
            String txnId = event.getTransactionId() != null && event.getTransactionId().length() > 6
                    ? event.getTransactionId().substring(0, 6) + "..."
                    : event.getTransactionId();

            String walletId = event.getWalletId() != null && event.getWalletId().length() > 6
                    ? event.getWalletId().substring(0, 6) + "..."
                    : event.getWalletId();

            String fromWalletId = event.getFromWalletId() != null && event.getFromWalletId().length() > 6
                    ? event.getFromWalletId().substring(0, 6) + "..."
                    : event.getFromWalletId();

            String toWalletId = event.getToWalletId() != null && event.getToWalletId().length() > 6
                    ? event.getToWalletId().substring(0, 6) + "..."
                    : event.getToWalletId();

            String balanceWalletName = event.getTransactionType() == TransactionType.CREDIT
                    ? event.getToWallet()
                    : event.getFromWallet();

            String body = "Dear " + (event.getFullName() != null ? event.getFullName() : "User") + ",\n\n" +
                    "We attempted to notify you via SMS regarding your recent transaction, " +
                    "but the SMS delivery failed. To ensure you stay informed, we are providing the full transaction details below:\n\n" +

                    " Transaction Summary:\n" +
                    "• Transaction ID: " + txnId + "\n" +
                    "• Wallet ID: " + walletId + "\n" +
                    "• Type: " + event.getTransactionType() + "\n" +
                    "• Level: " + event.getTransactionLevel() + "\n" +
                    "• From Wallet: " + (event.getFromWallet() != null ? event.getFromWallet() + " (" + fromWalletId + ")" : "N/A") + "\n" +
                    "• To Wallet: " + (event.getToWallet() != null ? event.getToWallet() + " (" + toWalletId + ")" : "N/A") + "\n" +
                    "• Amount: $" + String.format("%.2f", event.getAmount()) + "\n" +
                    "• Date & Time: " + event.getDateTime() + "\n" +
                    "• Current Balance (" + (balanceWalletName != null ? balanceWalletName : "Wallet") + "): $"
                    + (event.getRemainingBalance() != null ? String.format("%.2f", event.getRemainingBalance()) : "N/A") + "\n\n" +

                    " Status: The transaction has been successfully processed in our system.\n\n" +
                    " Note: This email is being sent as a fallback because the SMS alert could not be delivered. " +
                    "You can safely rely on this email for your transaction record.\n\n" +

                    "For any questions or if you notice discrepancies, please reach out to our support team immediately.\n\n" +

                    "Thank you for choosing SundukPay.\n\n" +
                    "Warm regards,\n" +
                    "SundukPay Team";

            emailService.sendEmail(event.getEmail(), subject, body);
        }  catch (Exception e) {
            log.error("Failed to send fallback email for Txn ID {}: {}", event.getTransactionId(), e.getMessage());
            throw new SmsServiceException("Failed to send SMS for transaction: " + event.getTransactionId());
        }
        log.info("Fallback email sent to: {}", event.getEmail());
    }
}

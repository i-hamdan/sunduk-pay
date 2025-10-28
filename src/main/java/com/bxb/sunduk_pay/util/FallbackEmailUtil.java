
        package com.bxb.sunduk_pay.util;

import com.bxb.sunduk_pay.exception.SmsServiceException;
import com.bxb.sunduk_pay.kafkaEvents.TransactionEvent;
import com.bxb.sunduk_pay.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

/**
 * Utility component responsible for sending fallback emails
 * when an SMS notification for a transaction cannot be delivered.
 *
 * <p>This ensures that the user still receives critical transaction
 * information via email even if the SMS fails.</p>
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class FallbackEmailUtil {

    /** Email service for sending emails. */
    private final EmailService emailService;

    /** Number of characters to show for IDs before trimming. */
    private static final int ID_TRIM_LENGTH = 6;

    /**
 * Sends a detailed fallback email to the user with transaction information
     * when the SMS notification fails.
     *
 * @param event the {@link TransactionEvent} containing transaction details
     * such as type, amount, wallet IDs, and user information.
     * @throws SmsServiceException if there is an error sending the email.
     */
    public void sendFallbackTransactionEmail(final TransactionEvent event) {
        try {
            String subject = "[Transaction Alert] SundukPay – "
                    + event.getTransactionType()
                    + " of $"
                    + String.format("%.2f", event.getAmount());

            // Trim IDs safely
            String txnId = event.getTransactionId() != null
                    && event.getTransactionId().length() > ID_TRIM_LENGTH
                    ? event.getTransactionId().substring(0, ID_TRIM_LENGTH)
                    + "..."
                    : event.getTransactionId();

            String walletId = event.getWalletId() != null
                    && event.getWalletId().length() > ID_TRIM_LENGTH
                    ? event.getWalletId().substring(0, ID_TRIM_LENGTH)
                    + "..."
                    : event.getWalletId();

            String fromWalletId = event.getFromWalletId() != null
                    && event.getFromWalletId().length() > ID_TRIM_LENGTH
                    ? event.getFromWalletId().substring(0, ID_TRIM_LENGTH)
                    + "..."
                    : event.getFromWalletId();

            String toWalletId = event.getToWalletId() != null
                    && event.getToWalletId().length() > ID_TRIM_LENGTH
                    ? event.getToWalletId().substring(0, ID_TRIM_LENGTH)
                    + "..."
                    : event.getToWalletId();

            String balanceWalletName =
                    event.getTransactionType() == TransactionType.CREDIT
                            ? event.getToWallet()
                            : event.getFromWallet();

            String body = "Dear "
                    + (event.getFullName() != null
                    ? event.getFullName()
                    : "User")
                    + ",\n\n"
                    + "We attempted to notify you via "
                    + "SMS regarding your recent "
                    + "transaction, but the SMS delivery failed."
                    +  "To ensure you stay "
                    + "informed, we are providing the full "
                    + "transaction details below:\n\n"
                    + " Transaction Summary:\n"
                    + "• Transaction ID: " + txnId + "\n"
                    + "• Wallet ID: " + walletId + "\n"
                    + "• Type: " + event.getTransactionType() + "\n"
                    + "• Level: " + event.getTransactionLevel() + "\n"
                    + "• From Wallet: "
                    + (event.getFromWallet() != null
                    ? event.getFromWallet() + " (" + fromWalletId + ")"
                    : "N/A")
                    + "\n"
                    + "• To Wallet: "
                    + (event.getToWallet() != null
                    ? event.getToWallet() + " (" + toWalletId + ")"
                    : "N/A")
                    + "\n"
                    + "• Amount: "
                    + String.format("%.2f", event.getAmount()) + "\n"
                    + "• Date & Time: " + event.getDateTime() + "\n"
                    + "• Current Balance ("
                    + (balanceWalletName != null
                    ? balanceWalletName
                    : "Wallet")
                    + "): "
                    + (event.getRemainingBalance() != null
                    ? String.format("%.2f", event.getRemainingBalance())
                    : "N/A")
                    + "\n\n"
                    + "Status: The transaction has been "
                    + "successfully processed in "
                    + "our system.\n\n"
                    + "Note: This email is being sent as "
                    + "a fallback because the SMS "
                    + "alert could not be delivered."
                    + "You can safely rely on this email "
                    + "for your transaction record.\n\n"
                    + "For any questions or if you "
                    + "notice discrepancies, please reach "
                    + "out to our support team immediately.\n\n"
                    + "Thank you for choosing SundukPay.\n\n"
                    + "Warm regards,\n"
                    + "SundukPay Team";

            emailService.sendEmail(event.getEmail(), subject, body,false);
        } catch (Exception e) {
            log.error(
                    "Failed to send fallback email for Txn ID {}: {}",
                    event.getTransactionId(),
                    e.getMessage()
            );
            throw new SmsServiceException(
                    "Failed to send SMS for transaction: "
                            + event.getTransactionId()
            );
        }
        log.info("Fallback email sent to: {}", event.getEmail());
    }
}

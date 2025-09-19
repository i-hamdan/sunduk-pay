package com.bxb.sunduk_pay.kafkaEvents;

import com.bxb.sunduk_pay.util.TransactionLevel;
import com.bxb.sunduk_pay.util.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a transaction event in the SundukPay system.
 * This class is used for Kafka messaging to convey transaction details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    private String transactionId;
    private String walletId;
    private Double amount;
    private TransactionType transactionType;
    private TransactionLevel transactionLevel;
    private String fromWallet;
    private String fromWalletId;
    private String toWallet;
    private String toWalletId;
    private LocalDateTime dateTime;
    private Double remainingBalance;
    private String uuid;
    private String email;
    private String fullName;
    private String phoneNumber;
}

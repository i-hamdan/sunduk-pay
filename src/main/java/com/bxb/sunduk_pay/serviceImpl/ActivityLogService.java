package com.bxb.sunduk_pay.serviceImpl;


import com.bxb.sunduk_pay.event.TransactionEvent;
import com.bxb.sunduk_pay.event.UserKafkaEvent;
import com.bxb.sunduk_pay.exception.TransactionLogException;
import com.bxb.sunduk_pay.exception.UserActivityLogException;
import com.bxb.sunduk_pay.logModel.TransactionLog;
import com.bxb.sunduk_pay.logModel.UserActivityLog;
import com.bxb.sunduk_pay.repository.TransactionLogRepository;
import com.bxb.sunduk_pay.repository.UserActivityLogRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Log4j2
public class ActivityLogService {
    private final UserActivityLogRepository activityLogRepository;
    private final TransactionLogRepository transactionLogRepository;

    public ActivityLogService(UserActivityLogRepository activityLogRepository, TransactionLogRepository transactionLogRepository) {
        this.activityLogRepository = activityLogRepository;
        this.transactionLogRepository = transactionLogRepository;
    }

    @KafkaListener(topics = "user-topic", groupId = "activity-log-group", concurrency = "3")
    public void consumeActivityLog(UserKafkaEvent userKafkaEvent) {
        try {
            log.info("Received user activity event: {}", userKafkaEvent);
            String description;
            if ("LOGIN".equals(userKafkaEvent.getEventType())) {
                description = "User '" + userKafkaEvent.getFullName() + "' successfully logged in to the system.";
            } else if ("SIGNUP".equals(userKafkaEvent.getEventType())) {
                description = "New user registration completed for '" + userKafkaEvent.getFullName() + "'.";
            } else {
                description = "User event received for '" + userKafkaEvent.getFullName() + "' with unknown action.";
            }

            UserActivityLog activityLog = UserActivityLog.builder()
                    .logId(UUID.randomUUID().toString())
                    .uuid(userKafkaEvent.getUuid())
                    .email(userKafkaEvent.getEmail())
                    .fullName(userKafkaEvent.getFullName())
                    .action(userKafkaEvent.getEventType())
                    .description(description)
                    .localDateTime(LocalDateTime.now())
                    .build();
            activityLogRepository.save(activityLog);
            log.info("Saved user activity log: {}", activityLog);
        } catch (Exception e) {
            log.error("Error saving user activity log for email: {}", userKafkaEvent.getEmail(), e);
            throw new UserActivityLogException("Failed to process user activity log for: " + userKafkaEvent.getEmail());
        }
    }

    @KafkaListener(topics = "transaction-topic", groupId = "transaction-log-group")
    public void consumeTransactionLog(TransactionEvent transactionEvent) {
        try {
            log.info("Received transaction event: {}", transactionEvent);

            TransactionLog transactionLog = TransactionLog.builder()
                    .logId(UUID.randomUUID().toString())
                    .uuid(transactionEvent.getUuid())
                    .walletId(transactionEvent.getWalletId())
                    .transactionId(transactionEvent.getTransactionId())
                    .dateTime(LocalDateTime.now())
                    .email(transactionEvent.getEmail())
                    .fullName(transactionEvent.getFullName())
                    .phoneNumber(transactionEvent.getPhoneNumber())
                    .amount(transactionEvent.getAmount())
                    .remainingAmount(transactionEvent.getRemainingAmount())
                    .transactionType(transactionEvent.getTransactionType())
                    .build();

            transactionLogRepository.save(transactionLog);
            log.info("Saved transaction log: {}", transactionLog);
        } catch (Exception e) {
            log.error("Error saving transaction log for txn ID: {}", transactionEvent.getTransactionId());
            throw new TransactionLogException("Failed to process transaction log for txn ID: " + transactionEvent.getTransactionId());
        }
    }
}
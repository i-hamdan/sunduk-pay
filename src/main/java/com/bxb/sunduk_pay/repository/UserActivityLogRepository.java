package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
/**
 * Repository interface for managing UserActivityLog document in MongoDB.
 */
public interface UserActivityLogRepository
        extends JpaRepository<UserActivityLog, String> {
}

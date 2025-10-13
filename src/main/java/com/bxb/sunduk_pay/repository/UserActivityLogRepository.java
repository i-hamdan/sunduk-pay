package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.logModel.UserActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;
/**
 * Repository interface for managing UserActivityLog document in MongoDB.
 */
public interface UserActivityLogRepository
        extends MongoRepository<UserActivityLog, String> {
}

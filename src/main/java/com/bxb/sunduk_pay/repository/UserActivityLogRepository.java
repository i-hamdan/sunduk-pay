package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.logModel.UserActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for persisting {@link UserActivityLog} entries.
 * <p>
 * This repository is primarily used for recording user actions
 * (e.g., login, signup). It is not intended for
 * standard CRUD operations.
 * </p>
 */
@Repository
public interface UserActivityLogRepository
        extends MongoRepository<UserActivityLog, String> {

    // Future custom query methods, for example:
    // List<UserActivityLog> findByUserId(String userId);
    // List<UserActivityLog> findByActionType(String actionType);
}

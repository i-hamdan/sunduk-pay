package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.logModel.UserActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserActivityLogRepository extends MongoRepository<UserActivityLog,String> {
}

package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.logModel.TransactionLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TransactionLogRepository extends MongoRepository<TransactionLog,String> {
}

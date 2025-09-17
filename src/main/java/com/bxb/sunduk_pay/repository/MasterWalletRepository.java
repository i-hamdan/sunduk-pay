package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.MainWallet;
import com.bxb.sunduk_pay.model.MasterWallet;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MasterWalletRepository extends MongoRepository<MasterWallet,String> {
    Optional<MasterWallet> findByUser_Uuid(String uuid);
}

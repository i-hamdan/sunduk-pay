package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.MainWallet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MainWalletRepository extends MongoRepository<MainWallet, String> {
    Optional<MainWallet> findByUser_Uuid(String uuid);
}

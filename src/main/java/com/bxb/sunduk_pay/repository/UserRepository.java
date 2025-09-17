package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    User findByUuid(String uuid);
}

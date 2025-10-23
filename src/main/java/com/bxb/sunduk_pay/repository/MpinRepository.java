package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.Mpin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MpinRepository extends JpaRepository<Mpin,Long> {

    Optional<Mpin> findByUser_Uuid(String uuid);
}

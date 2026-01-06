package com.bxb.sunduk_pay.repository;

import com.bxb.sunduk_pay.model.GlobalPotDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GlobalPotDocumentRepository extends JpaRepository<GlobalPotDocument, String> {
    List<GlobalPotDocument> findByGlobalPotGlobalPotId(String globalPotId);
}

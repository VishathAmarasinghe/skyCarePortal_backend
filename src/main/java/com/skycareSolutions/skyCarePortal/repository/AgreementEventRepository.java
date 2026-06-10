package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.AgreementEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AgreementEventRepository extends JpaRepository<AgreementEvent, String> {

    java.util.List<AgreementEvent> findByInstanceIdOrderByCreatedAtDesc(String instanceId);

    @Query(value = "SELECT id FROM agreement_event ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String getLastId();
}

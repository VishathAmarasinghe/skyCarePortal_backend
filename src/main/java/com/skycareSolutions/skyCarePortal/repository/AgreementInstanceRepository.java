package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.AgreementInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AgreementInstanceRepository extends JpaRepository<AgreementInstance, String> {

    @Query("SELECT i FROM AgreementInstance i "
            + "JOIN FETCH i.templateVersion tv "
            + "JOIN FETCH tv.template "
            + "WHERE i.id = :id")
    Optional<AgreementInstance> findDetailedById(String id);

    List<AgreementInstance> findByRecipientTypeAndRecipientIdOrderByCreatedAtDesc(
            String recipientType, String recipientId);

    Optional<AgreementInstance> findBySigningTokenHash(String signingTokenHash);

    @Query("SELECT i FROM AgreementInstance i WHERE i.status IN :statuses AND i.expiresAt > :now")
    List<AgreementInstance> findPendingSignatureInstances(
            @org.springframework.data.repository.query.Param("statuses") List<String> statuses,
            @org.springframework.data.repository.query.Param("now") java.time.LocalDateTime now);

    @Query(value = "SELECT id FROM agreement_instance ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String getLastId();
}

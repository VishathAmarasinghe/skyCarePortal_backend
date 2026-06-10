package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.RestrictivePracticeEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RestrictivePracticeEvidenceRepository extends JpaRepository<RestrictivePracticeEvidence, String> {
    @Query(value = "SELECT e.evidenceid FROM restrictive_practice_evidence e ORDER BY CAST(SUBSTRING(e.evidenceid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastEvidenceId();
}

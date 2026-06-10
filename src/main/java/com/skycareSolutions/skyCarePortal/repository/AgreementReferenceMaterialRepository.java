package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.AgreementReferenceMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AgreementReferenceMaterialRepository extends JpaRepository<AgreementReferenceMaterial, String> {

    @Query(value = "SELECT id FROM agreement_reference_material ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String getLastId();

    List<AgreementReferenceMaterial> findByActiveTrueOrderBySortOrderAscTitleAsc();

    @Query("""
            SELECT r FROM AgreementReferenceMaterial r
            WHERE r.active = true
            AND (r.audience = 'BOTH' OR r.audience = :audience)
            AND (:careGiverType IS NULL OR r.careGiverType IS NULL OR r.careGiverType = :careGiverType)
            ORDER BY r.sortOrder ASC, r.title ASC
            """)
    List<AgreementReferenceMaterial> findAvailableForRecipient(
            @Param("audience") String audience,
            @Param("careGiverType") String careGiverType
    );
}

package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.CareGiverDocumentTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CareGiverDocumentTypesRepository extends JpaRepository<CareGiverDocumentTypes, String> {
    @Query(value = "SELECT dt.document_typeid FROM caregiver_document_types dt ORDER BY CAST(SUBSTRING(dt.document_typeid, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfdocumentType();
}

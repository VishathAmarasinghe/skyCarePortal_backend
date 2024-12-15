package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.IncidentDocuments;
import com.skycareSolutions.skyCarePortal.model.Incidents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IncidentDocumentsRepository extends JpaRepository<IncidentDocuments,String> {

    @Query("SELECT doc FROM IncidentDocuments doc WHERE doc.incidents=:incidents")
    List<IncidentDocuments> getAllDocumentsRelatedToIncident(Incidents incidents);

    @Query(value = "SELECT doc.document_id FROM incident_documents doc ORDER BY CAST(SUBSTRING(doc.document_id, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfIncidentDocument();
}

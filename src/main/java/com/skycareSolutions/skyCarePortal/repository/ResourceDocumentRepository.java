package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.Resource;
import com.skycareSolutions.skyCarePortal.model.ResourceDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResourceDocumentRepository extends JpaRepository<ResourceDocument, String> {
    @Query(value = "SELECT rd.document_id FROM resource_document rd ORDER BY CAST(SUBSTRING(rd.document_id, 3) AS UNSIGNED) DESC LIMIT 1", nativeQuery = true)
    String getLastIdOfResourceDocument();

    @Query("SELECT rd from ResourceDocument rd where rd.resource=:resource")
    List<ResourceDocument> getAllResourceDocumentsByResource(Resource resource);
}

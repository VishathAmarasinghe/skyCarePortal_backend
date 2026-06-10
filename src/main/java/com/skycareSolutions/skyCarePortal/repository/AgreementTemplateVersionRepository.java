package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.AgreementTemplateVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AgreementTemplateVersionRepository extends JpaRepository<AgreementTemplateVersion, String> {

    List<AgreementTemplateVersion> findByTemplate_IdOrderByVersionNumberDesc(String templateId);

    Optional<AgreementTemplateVersion> findByTemplate_IdAndStatus(String templateId, String status);

    @Query("SELECT MAX(v.versionNumber) FROM AgreementTemplateVersion v WHERE v.template.id = :templateId")
    Integer findMaxVersionNumber(@Param("templateId") String templateId);

    @Query(value = "SELECT id FROM agreement_template_version ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String getLastId();
}

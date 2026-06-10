package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.AgreementTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AgreementTemplateRepository extends JpaRepository<AgreementTemplate, String> {

    Optional<AgreementTemplate> findByTemplateKey(String templateKey);

    List<AgreementTemplate> findByAudience(String audience);

    List<AgreementTemplate> findByAudienceAndCareGiverType(String audience, String careGiverType);

    @Query(value = "SELECT id FROM agreement_template ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String getLastId();
}

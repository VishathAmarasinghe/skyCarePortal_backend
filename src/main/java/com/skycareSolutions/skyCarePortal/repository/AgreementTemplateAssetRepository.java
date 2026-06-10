package com.skycareSolutions.skyCarePortal.repository;

import com.skycareSolutions.skyCarePortal.model.AgreementTemplateAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AgreementTemplateAssetRepository extends JpaRepository<AgreementTemplateAsset, String> {

    @Query(value = "SELECT id FROM agreement_template_asset ORDER BY id DESC LIMIT 1", nativeQuery = true)
    String getLastId();
}
